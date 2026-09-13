package com.androidautharchitecture.data.payment.repository

import com.androidautharchitecture.BuildConfig
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.data.network.safeApiCall
import com.androidautharchitecture.data.payment.remote.api.PaymentApi
import com.androidautharchitecture.data.payment.remote.dto.CreateOrderRequestDto
import com.androidautharchitecture.data.payment.remote.dto.VerifyPaymentRequestDto
import com.androidautharchitecture.domain.payment.model.PaymentOrder
import com.androidautharchitecture.domain.payment.model.PaymentVerification
import com.androidautharchitecture.domain.payment.repository.PaymentRepository
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val api: PaymentApi
) : PaymentRepository {

    override suspend fun createOrder(
        amountInPaisa: Long,
        currency: String
    ): AppResult<PaymentOrder> {
        if (BuildConfig.BASE_URL.contains("example.com")) {
            val mockOrderId = "order_mock_" + UUID.randomUUID().toString().replace("-", "").take(12)
            Timber.d("Generated mock Razorpay order ID: $mockOrderId")
            return AppResult.Success(
                PaymentOrder(
                    orderId = mockOrderId,
                    amountInPaisa = amountInPaisa,
                    currency = currency,
                    keyId = BuildConfig.RAZORPAY_KEY_ID
                )
            )
        }

        return when (val result = safeApiCall { api.createOrder(CreateOrderRequestDto(amount = amountInPaisa, currency = currency)) }) {
            is AppResult.Success -> {
                AppResult.Success(
                    PaymentOrder(
                        orderId = result.data.orderId,
                        amountInPaisa = result.data.amount,
                        currency = result.data.currency,
                        keyId = result.data.keyId.ifBlank { BuildConfig.RAZORPAY_KEY_ID }
                    )
                )
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun verifyPayment(
        orderId: String,
        paymentId: String,
        signature: String
    ): AppResult<PaymentVerification> {
        if (BuildConfig.BASE_URL.contains("example.com")) {
            Timber.d("Mock verification success for orderId=$orderId, paymentId=$paymentId")
            return AppResult.Success(
                PaymentVerification(
                    isSuccess = true,
                    message = "Payment verified successfully (Mock Backend Verification)"
                )
            )
        }

        return when (val result = safeApiCall {
            api.verifyPayment(
                VerifyPaymentRequestDto(
                    orderId = orderId,
                    paymentId = paymentId,
                    signature = signature
                )
            )
        }) {
            is AppResult.Success -> {
                AppResult.Success(
                    PaymentVerification(
                        isSuccess = result.data.success,
                        message = result.data.message
                    )
                )
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
