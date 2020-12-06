package io.pleo.antaeus.core.external.billing

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun processInvoice(paymentProvider: PaymentProvider,
                   invoiceService: InvoiceService,
                   invoice: Invoice) {

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
        logger.info { "updating invoice.id = $invoice.id to status $invoiceProcessResult" }
        invoiceService.updateInvoice(invoice.copy(status = invoiceProcessResult))
    }
}
