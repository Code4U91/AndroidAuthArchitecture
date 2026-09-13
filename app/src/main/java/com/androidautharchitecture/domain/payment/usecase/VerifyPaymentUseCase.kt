package com.androidautharchitecture.domain.payment.usecase

import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.payment.model.PaymentVerification
import com.androidautharchitecture.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class VerifyPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        orderId: String,
        paymentId: String,
        signature: String
    ): AppResult<PaymentVerification> {
        return repository.verifyPayment(orderId, paymentId, signature)
    }
}
