package com.androidautharchitecture.presentation.payment

sealed interface PaymentUiState {
    data object Idle : PaymentUiState
    data object CreatingOrder : PaymentUiState
    data class AwaitingPayment(val orderId: String) : PaymentUiState
    data object VerifyingPayment : PaymentUiState
    data class Success(val paymentId: String, val message: String) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}