package com.androidautharchitecture.data.payment.sdk

import android.app.Activity
import com.androidautharchitecture.BuildConfig
import com.androidautharchitecture.domain.payment.gateway.PaymentGateway
import com.androidautharchitecture.domain.payment.model.PaymentResult
import com.razorpay.Checkout
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [PaymentGateway] using the official Razorpay Android SDK.
 * Lives in the Data/Infrastructure layer.
 */
@Singleton
class RazorpayPaymentGateway @Inject constructor(
) : PaymentGateway {

    private val _paymentResult = MutableSharedFlow<PaymentResult>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val paymentResult: SharedFlow<PaymentResult> = _paymentResult.asSharedFlow()

    override fun openCheckout(
        activity: Activity,
        orderId: String,
        amountInPaisa: Long,
        currency: String,
        name: String,
        description: String,
        userEmail: String,
        userContact: String
    ) {
        val checkout = Checkout()
        val keyId = BuildConfig.RAZORPAY_KEY_ID
        if (keyId.isNotBlank()) {
            checkout.setKeyID(keyId)
        }

        try {
            val options = JSONObject().apply {
                put("name", name)
                put("description", description)
                put("currency", currency)
                put("amount", amountInPaisa)

                // Razorpay SDK validates order_id against Razorpay servers.
                // For mock order IDs (without a real backend order creation API),
                // omit "order_id" so Razorpay opens in Standalone Client Test Mode!
                if (orderId.isNotBlank() && !orderId.startsWith("order_mock_")) {
                    put("order_id", orderId)
                }

                val prefill = JSONObject().apply {
                    if (userEmail.isNotBlank()) put("email", userEmail)
                    if (userContact.isNotBlank()) put("contact", userContact)
                }
                put("prefill", prefill)

                val retry = JSONObject().apply {
                    put("enabled", true)
                    put("max_count", 2)
                }
                put("retry", retry)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            Timber.e(e, "Error launching Razorpay checkout UI")
            _paymentResult.tryEmit(
                PaymentResult.Error(
                    code = -1,
                    description = e.localizedMessage ?: "Failed to launch payment checkout",
                    orderId = orderId
                )
            )
        }
    }

    override fun handlePaymentSuccess(paymentId: String, orderId: String, signature: String) {
        Timber.d("Razorpay Payment Success: paymentId=$paymentId, orderId=$orderId")
        _paymentResult.tryEmit(
            PaymentResult.Success(
                paymentId = paymentId,
                orderId = orderId,
                signature = signature
            )
        )
    }

    override fun handlePaymentError(errorCode: Int, description: String, orderId: String?) {
        Timber.e("Razorpay Payment Error: code=$errorCode, description=$description")
        if (errorCode == Checkout.PAYMENT_CANCELED) {
            _paymentResult.tryEmit(PaymentResult.Cancelled)
        } else {
            _paymentResult.tryEmit(
                PaymentResult.Error(
                    code = errorCode,
                    description = description,
                    orderId = orderId
                )
            )
        }
    }
}
