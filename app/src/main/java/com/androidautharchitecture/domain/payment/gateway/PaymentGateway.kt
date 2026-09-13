package com.androidautharchitecture.domain.payment.gateway

import android.app.Activity
import com.androidautharchitecture.domain.payment.model.PaymentResult
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface defining the payment gateway contract.
 * Lives in the Domain layer to keep app business logic decoupled from specific payment SDKs.
 */
interface PaymentGateway {
    val paymentResult: SharedFlow<PaymentResult>

    fun openCheckout(
        activity: Activity,
        orderId: String,
        amountInPaisa: Long,
        currency: String = "INR",
        name: String = "Android Auth Store",
        description: String = "Order Payment",
        userEmail: String = "",
        userContact: String = ""
    )

    fun handlePaymentSuccess(paymentId: String, orderId: String, signature: String)
    fun handlePaymentError(errorCode: Int, description: String, orderId: String?)
}
