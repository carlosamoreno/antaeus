package io.pleo.antaeus.core.external.billing

import com.google.gson.Gson
import com.rabbitmq.client.Channel
import io.pleo.antaeus.models.Invoice

class BillingInvoiceProducer(private val channel: Channel) {

    fun queueInvoice(invoice: Invoice) {
        val invoiceJson = Gson().toJson(invoice)
        channel.basicPublish("","antaeus", null, invoiceJson.toByteArray())
    }
}