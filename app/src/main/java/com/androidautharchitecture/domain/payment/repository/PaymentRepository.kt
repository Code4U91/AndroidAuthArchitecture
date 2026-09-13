package com.androidautharchitecture.domain.payment.repository

import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.payment.model.PaymentOrder
import com.androidautharchitecture.domain.payment.model.PaymentVerification

/**
 * Repository interface for payment order creation and signature verification.
 */
interface PaymentRepository {

    /**
     * Calls backend API to create an order with Razorpay.
     */
    suspend fun createOrder(
        amountInPaisa: Long,
        currency: String = "INR"
    ): AppResult<PaymentOrder>

    /**
     * Calls backend API to verify HMAC-SHA256 signature after client checkout.
     */
    suspend fun verifyPayment(
        orderId: String,
        paymentId: String,
        signature: String
    ): AppResult<PaymentVerification>
}
