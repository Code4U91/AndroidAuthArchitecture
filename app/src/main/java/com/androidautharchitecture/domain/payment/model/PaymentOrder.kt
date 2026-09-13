package com.androidautharchitecture.domain.payment.model

/**
 * Domain entity representing an order created on the backend for Razorpay checkout.
 */
data class PaymentOrder(
    val orderId: String,
    val amountInPaisa: Long,
    val currency: String = "INR",
    val keyId: String = ""
)

/**
 * Domain entity representing backend payment signature verification result.
 */
data class PaymentVerification(
    val isSuccess: Boolean,
    val message: String
)
