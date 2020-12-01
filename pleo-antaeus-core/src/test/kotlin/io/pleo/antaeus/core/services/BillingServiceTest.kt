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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BillingServiceTest {

    private val actualInvoice: Invoice = Invoice(1, 1, Money(BigDecimal(100.0), Currency.EUR), InvoiceStatus.PENDING)
    private lateinit var invoiceService: InvoiceService
    private lateinit var paymentProvider: PaymentProvider
    private lateinit var billingService: BillingService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        invoiceService = mockInvoiceServiceWithData(actualInvoice)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when the chage is OK then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultOk(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)

        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.PAID)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when the customes doesn't have balance then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultNotEnoughFounds(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)
        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_NO_BALANCE)) }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when the customes is not found then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultCustomerNotFound(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)
        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_CUSTOMER_NOT_FOUND)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when the currency is different as expected then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultCurrencyMismatch(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)
        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_CURRENCY_MISMATCH)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when an network error happens then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultNetworkException(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)
        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_NETWORK)) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given unpaid invoices when an unexpected error happens then the database is updated `() {
        paymentProvider = mockPaymentProviderWithResultUnexpectedError(actualInvoice)
        billingService = BillingService(paymentProvider, invoiceService)
        whenRunningBillingServices()
        verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(InvoiceStatus.ERROR_UNKNOWN)) }
    }
    ///////////////////
    //Auxiliary methods to make the tests easier to read
    ///////////////////

    @ExperimentalCoroutinesApi
    private fun whenRunningBillingServices() {
        runBlockingTest {
            billingService.processPendingInvoices()
        }
    }

    private fun expectedInvoice(invoiceStatus: InvoiceStatus) = actualInvoice.copy(status = invoiceStatus)

    private fun thenDatabaseIsUpdatedWithInvoiceStatus(invoiceService:InvoiceService, invoiceStatus: InvoiceStatus) =
            verify(exactly = 1) { invoiceService.updateInvoice(expectedInvoice(invoiceStatus)) }

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

}
