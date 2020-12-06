package io.pleo.antaeus.app

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.data.CustomerDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import kotlin.random.Random

// This will create all schemas and setup initial data
internal fun setupInitialData(customerDal: CustomerDal, invoiceDal: InvoiceDal) {
    val customers = (1..100).mapNotNull {
        customerDal.createCustomer(
            currency = Currency.values()[Random.nextInt(0, Currency.values().size)]
        )
    }

    customers.forEach { customer ->
        (1..10).forEach {
            invoiceDal.createInvoice(
                amount = Money(
                    value = BigDecimal(Random.nextDouble(10.0, 500.0)),
                    currency = customer.currency
                ),
                customer = customer,
                status = if (it == 1) InvoiceStatus.PENDING else InvoiceStatus.PAID
            )
        }
    }
}

// This will create the connection with the queue
internal fun setupQueue(): Channel {
    val connectionFactory = ConnectionFactory()
    connectionFactory.host = "queue"
    val channel = connectionFactory.newConnection().createChannel()
    channel.queueDeclare("antaeus", false, false, false, null)
    channel.basicQos(1) // Max number of messages delivered to every worker

    return channel
}

// This is the mocked instance of the payment provider
internal fun getPaymentProvider(): PaymentProvider {
    return object : PaymentProvider {
        override fun charge(invoice: Invoice): Boolean {
            return when (Random.nextInt(5)) {
                0 -> true
                1 -> false
                2 -> throw CustomerNotFoundException(invoice.id)
                3 -> throw CurrencyMismatchException(invoice.id, invoice.customerId)
                4 -> throw NetworkException()
                else -> throw Exception()
            }
        }
    }
}
