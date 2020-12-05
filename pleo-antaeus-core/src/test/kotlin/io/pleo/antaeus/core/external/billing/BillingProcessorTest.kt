package io.pleo.antaeus.core.external.billing

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillingProcessorTest {

    private val actualInvoice: Invoice = Invoice(1, 1, Money(BigDecimal(100.0), Currency.EUR), InvoiceStatus.PENDING)
    private lateinit var invoiceService: InvoiceService
    private lateinit var paymentProvider: PaymentProvider

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `processing invoices successfully`() {
        `given a payment provider with result Ok`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.PAID)
    }

    @Test
    fun `processing invoices of customers with no balance`() {
        `given a payment provider with result no balance`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.ERROR_NO_BALANCE)
    }

    @Test
    fun `processing invoices of customers with customer not found`() {
        `given a payment provider with result customer not found`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.ERROR_CUSTOMER_NOT_FOUND)
    }

    @Test
    fun `processing invoices of customers with currency mismatch`() {
        `given a payment provider with result currency mismatch`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.ERROR_CURRENCY_MISMATCH)
    }

    @Test
    fun `processing invoices of customers with network error`() {
        `given a payment provider with result network error`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.ERROR_NETWORK)
    }

    @Test
    fun `processing invoices of customers with an unknown error`() {
        `given a payment provider with result unknown error`()
        `and given an invoice service with the expected invoice`()
        `when processInvoice is executed`()
        `then the database is updated with status`(InvoiceStatus.ERROR_UNKNOWN)
    }


    ///////////////////
    //Auxiliary methods to make the tests easier to read
    ///////////////////

    private fun expectedInvoice(invoiceStatus: InvoiceStatus) = actualInvoice.copy(status = invoiceStatus)

    private fun `given a payment provider with result Ok`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } returns true
        }
    }

    private fun `given a payment provider with result no balance`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } returns false
        }
    }

    private fun `given a payment provider with result customer not found`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } throws CustomerNotFoundException(actualInvoice.customerId)
        }
    }

    private fun `given a payment provider with result currency mismatch`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } throws CurrencyMismatchException(actualInvoice.id, actualInvoice.customerId)
        }
    }

    private fun `given a payment provider with result network error`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } throws NetworkException()
        }
    }

    private fun `given a payment provider with result unknown error`() {
        paymentProvider = mockk {
            every { charge(actualInvoice) } throws Exception()
        }
    }

    private fun `and given an invoice service with the expected invoice`() {
        invoiceService = mockk {
            every { updateInvoice(any()) } returns 0
        }
    }

    private fun `when processInvoice is executed`() {
        processInvoice(paymentProvider, invoiceService, actualInvoice)
    }

    private fun `then the database is updated with status`(invoiceStatus: InvoiceStatus) {
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(invoiceStatus)) }
    }

}
