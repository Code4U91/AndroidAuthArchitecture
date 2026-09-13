package com.androidautharchitecture.data.payment.remote.api

import com.androidautharchitecture.data.payment.remote.dto.CreateOrderRequestDto
import com.androidautharchitecture.data.payment.remote.dto.CreateOrderResponseDto
import com.androidautharchitecture.data.payment.remote.dto.VerifyPaymentRequestDto
import com.androidautharchitecture.data.payment.remote.dto.VerifyPaymentResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {

    @POST("api/v1/payment/create-order")
    suspend fun createOrder(
        @Body request: CreateOrderRequestDto
    ): CreateOrderResponseDto

    @POST("api/v1/payment/verify")
    suspend fun verifyPayment(
        @Body request: VerifyPaymentRequestDto
    ): VerifyPaymentResponseDto
}
