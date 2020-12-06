package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.billing.BillingInvoiceProducer
import io.pleo.antaeus.models.InvoiceStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

class BillingService(
        private val invoiceService: InvoiceService,
        private val billingInvoiceProducer: BillingInvoiceProducer
): CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val logger = KotlinLogging.logger {}

    fun processPendingInvoices() = launch { processInvoicesByStatus(InvoiceStatus.PENDING) }
    fun processNetworkErrorInvoices() = launch { processInvoicesByStatus(InvoiceStatus.ERROR_NETWORK) }

    private fun processInvoicesByStatus(status: InvoiceStatus) {
        logger.info { "queueing billing services for status $status" }
        invoiceService
                .fetchByStatus(status)
                .forEach { invoice ->
                    billingInvoiceProducer.queueInvoice(invoice)
                }
    }

}
