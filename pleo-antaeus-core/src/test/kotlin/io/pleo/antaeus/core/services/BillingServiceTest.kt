package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillingServiceTest {

    /*private val actualInvoice: Invoice = Invoice(1, 1, Money(BigDecimal(100.0), Currency.EUR), InvoiceStatus.PENDING)
    private lateinit var invoiceService: InvoiceService
    private lateinit var billingService: BillingService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        invoiceService = mockInvoiceServiceWithData(actualInvoice)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices successfully`() {
        `given a billing service with an invoice that is charged succesfully`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.PAID)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices with no balance`() {
        `given a billing service with a customer with no enough balance`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_NO_BALANCE)) }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices with an unexisting customer`() {
        `given a billing service with a customer not found invoice`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_CUSTOMER_NOT_FOUND)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices with currency mismatch`() {
        `given a billing service with a currency mismatch error invoice`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_CURRENCY_MISMATCH)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices with network error`() {
        `given a billing service with a network error invoice`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_NETWORK)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `processing invoices with an unexpected error`() {
        `given a billing service with an unexpected error invoice`()
        `when billing services processes the invoice`()
        verify(exactly = 1) { invoiceService.updateInvoice(any()) }
    }

    ///////////////////
    //Auxiliary methods to make the tests easier to read
    ///////////////////

    @ExperimentalCoroutinesApi
    private fun `when billing services processes the invoice`() {
        runBlockingTest {
            billingService.processPendingInvoices()
        }
    }

    private fun `given a billing service with an invoice that is charged succesfully`() {
        billingService = BillingService(mockPaymentProviderWithResultOk(actualInvoice), invoiceService)
    }
    private fun `given a billing service with a customer with no enough balance`() {
        billingService = BillingService(mockPaymentProviderWithResultNotEnoughFounds(actualInvoice), invoiceService)
    }
    private fun `given a billing service with a customer not found invoice`() {
        billingService = BillingService(mockPaymentProviderWithResultCustomerNotFound(actualInvoice), invoiceService)
    }
    private fun `given a billing service with a currency mismatch error invoice`() {
        billingService = BillingService(mockPaymentProviderWithResultCurrencyMismatch(actualInvoice), invoiceService)
    }
    private fun `given a billing service with an unexpected error invoice`() {
        billingService = BillingService(mockPaymentProviderWithResultUnexpectedError(actualInvoice), invoiceService)
    }
    private fun `given a billing service with a network error invoice`() {
        billingService = BillingService(mockPaymentProviderWithResultNetworkException(actualInvoice), invoiceService)
    }
    private fun expectedInvoice(invoiceStatus: InvoiceStatus) = actualInvoice.copy(status = invoiceStatus)

    private fun mockInvoiceServiceWithData(invoice: Invoice): InvoiceService {
        return mockk {
            every { fetchByStatus(InvoiceStatus.PENDING) } returns listOf(invoice)
            every { updateInvoice(invoice) } returns 0
        }
    }

    private fun mockPaymentProviderWithResultOk(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } returns true
    }

    private fun mockPaymentProviderWithResultNotEnoughFounds(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } returns false
    }

    private fun mockPaymentProviderWithResultCustomerNotFound(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } throws CustomerNotFoundException(id = invoice.id)
    }

    private fun mockPaymentProviderWithResultCurrencyMismatch(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } throws CurrencyMismatchException(invoiceId = invoice.id, customerId = invoice.customerId)
    }

    private fun mockPaymentProviderWithResultNetworkException(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } throws NetworkException()
    }

    private fun mockPaymentProviderWithResultUnexpectedError(invoice: Invoice) = mockk<PaymentProvider> {
        every { charge(invoice) } throws Exception()
    }
*/
}
