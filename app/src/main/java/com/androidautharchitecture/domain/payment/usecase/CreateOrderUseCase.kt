package com.androidautharchitecture.domain.payment.usecase

import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.payment.model.PaymentOrder
import com.androidautharchitecture.domain.payment.repository.PaymentRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        amountInPaisa: Long,
        currency: String = "INR"
    ): AppResult<PaymentOrder> {
        return repository.createOrder(amountInPaisa, currency)
    }
}
