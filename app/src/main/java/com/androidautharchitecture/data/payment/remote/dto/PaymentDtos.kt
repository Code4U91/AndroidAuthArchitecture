package com.androidautharchitecture.data.payment.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequestDto(
    @SerialName("amount") val amount: Long,
    @SerialName("currency") val currency: String = "INR"
)

@Serializable
data class CreateOrderResponseDto(
    @SerialName("orderId") val orderId: String,
    @SerialName("amount") val amount: Long,
    @SerialName("currency") val currency: String,
    @SerialName("keyId") val keyId: String = ""
)

@Serializable
data class VerifyPaymentRequestDto(
    @SerialName("orderId") val orderId: String,
    @SerialName("paymentId") val paymentId: String,
    @SerialName("signature") val signature: String
)

@Serializable
data class VerifyPaymentResponseDto(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String
)
