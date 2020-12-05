package io.pleo.antaeus.core.external.billing

import com.google.gson.Gson
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

class BillingInvoiceConsumer (private val channel: Channel,
                              private val paymentProvider: PaymentProvider,
                              private val invoiceService: InvoiceService) {

    private val logger = KotlinLogging.logger {}

    private val consumer = object : DefaultConsumer(channel) {
        override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray) {
            val message = String(body, charset("UTF-8"))
            val invoice = Gson().fromJson(message, Invoice::class.java)
            logger.info(" [x] Received '$message'")
            try {
                processInvoice(paymentProvider, invoiceService, invoice)
            } finally {
                channel.basicAck(envelope.deliveryTag, false)
            }
        }
    }

    fun registerConsumer(): String = channel.basicConsume("antaeus", false, consumer)

}