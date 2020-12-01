package io.pleo.antaeus.models

enum class InvoiceStatus {
    PENDING,
    PAID,
    ERROR_NO_BALANCE,
    ERROR_CUSTOMER_NOT_FOUND,
    ERROR_CURRENCY_MISMATCH,
    ERROR_NETWORK,
    ERROR_UNKNOWN
}
