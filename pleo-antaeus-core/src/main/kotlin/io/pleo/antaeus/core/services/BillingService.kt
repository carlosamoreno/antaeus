package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus
import java.lang.Exception
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val invoiceService: InvoiceService
): CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    fun processPendingInvoices() = launch { processInvoicesByStatus(InvoiceStatus.PENDING) }
    fun processNetworkErrorInvoices() = launch { processInvoicesByStatus(InvoiceStatus.ERROR_NETWORK) }

    private fun processInvoicesByStatus(status: InvoiceStatus) {
        invoiceService
                .fetchByStatus(status)
                .forEach { invoice ->
                    var invoiceProcessResult = invoice.status
                    try {
                        invoiceProcessResult = if (paymentProvider.charge(invoice))
                            InvoiceStatus.PAID
                        else
                            InvoiceStatus.ERROR_NO_BALANCE

                    } catch (err: Exception) {
                        invoiceProcessResult = when (err) {
                            is CustomerNotFoundException -> InvoiceStatus.ERROR_CUSTOMER_NOT_FOUND
                            is CurrencyMismatchException -> InvoiceStatus.ERROR_CURRENCY_MISMATCH
                            is NetworkException -> InvoiceStatus.ERROR_NETWORK
                            else -> InvoiceStatus.ERROR_UNKNOWN
                        }
                    } finally {
                        invoiceService.updateInvoice(invoice.copy(status = invoiceProcessResult))
                    }

                }

    }

}
