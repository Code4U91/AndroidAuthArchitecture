package com.androidautharchitecture.presentation.payment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.payment.gateway.PaymentGateway
import com.androidautharchitecture.domain.payment.model.PaymentResult
import com.androidautharchitecture.domain.payment.usecase.CreateOrderUseCase
import com.androidautharchitecture.domain.payment.usecase.VerifyPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val verifyPaymentUseCase: VerifyPaymentUseCase,
    private val paymentGateway: PaymentGateway
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        observePaymentResults()
    }

    private fun observePaymentResults() {
        viewModelScope.launch {
            paymentGateway.paymentResult.collect { result ->
                when (result) {
                    is PaymentResult.Success -> {
                        verifyPaymentSignature(result.orderId, result.paymentId, result.signature)
                    }
                    is PaymentResult.Error -> {
                        _uiState.value = PaymentUiState.Error(result.description)
                    }
                    is PaymentResult.Cancelled -> {
                        _uiState.value = PaymentUiState.Idle
                    }
                }
            }
        }
    }

    fun startPayment(activity: Activity, amountInRupees: Double) {
        val amountInPaisa = (amountInRupees * 100).toLong()
        _uiState.value = PaymentUiState.CreatingOrder

        viewModelScope.launch {
            when (val orderResult = createOrderUseCase(amountInPaisa = amountInPaisa)) {
                is AppResult.Success -> {
                    val order = orderResult.data
                    _uiState.value = PaymentUiState.AwaitingPayment(order.orderId)
                    
                    paymentGateway.openCheckout(
                        activity = activity,
                        orderId = order.orderId,
                        amountInPaisa = order.amountInPaisa,
                        currency = order.currency,
                        name = "Android Auth Store",
                        description = "Pro Plan Subscription",
                        userEmail = "user@example.com",
                        userContact = "9876543210"
                    )
                }
                is AppResult.Failure -> {
                    _uiState.value = PaymentUiState.Error("Failed to create order on server.")
                }
            }
        }
    }

    private fun verifyPaymentSignature(orderId: String, paymentId: String, signature: String) {
        _uiState.value = PaymentUiState.VerifyingPayment

        viewModelScope.launch {
            when (val verificationResult = verifyPaymentUseCase(orderId, paymentId, signature)) {
                is AppResult.Success -> {
                    if (verificationResult.data.isSuccess) {
                        _uiState.value = PaymentUiState.Success(
                            paymentId = paymentId,
                            message = verificationResult.data.message
                        )
                    } else {
                        _uiState.value = PaymentUiState.Error("Payment verification failed on server.")
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = PaymentUiState.Error("Server verification failed.")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = PaymentUiState.Idle
    }
}
