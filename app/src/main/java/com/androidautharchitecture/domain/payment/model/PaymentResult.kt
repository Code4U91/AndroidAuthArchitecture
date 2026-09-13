package com.androidautharchitecture.domain.payment.model

/**
 * Pure Kotlin domain representation of Razorpay payment outcome.
 * Free from any 3rd-party vendor SDK classes (e.g. Razorpay PaymentData).
 */
sealed interface PaymentResult {

    /**
     * Payment completed successfully on client side.
     * Note: [signature] MUST be verified on server side before fulfilling order.
     */
    data class Success(
        val paymentId: String,
        val orderId: String,
        val signature: String
    ) : PaymentResult

    /**
     * Payment failed due to card decline, network failure, or SDK error.
     */
    data class Error(
        val code: Int,
        val description: String,
        val orderId: String? = null
    ) : PaymentResult

    /**
     * User cancelled or dismissed the payment dialog.
     */
    data object Cancelled : PaymentResult
}
