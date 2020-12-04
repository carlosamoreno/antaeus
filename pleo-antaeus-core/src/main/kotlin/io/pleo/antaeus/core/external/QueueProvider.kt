package io.pleo.antaeus.core.external

import com.google.gson.Gson
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

class QueueProvider(private val channel: Channel) {

    fun queueInvoice(invoice: Invoice) {
        val invoiceJson = Gson().toJson(invoice)
        channel.basicPublish("","antaeus", null, invoiceJson.toByteArray())
    }

    fun dequeueInvoice(consumer: DefaultConsumer) {
        channel.basicConsume("antaeus", true, consumer)
    }
}