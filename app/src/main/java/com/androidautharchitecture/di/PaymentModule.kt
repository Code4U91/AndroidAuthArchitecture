package com.androidautharchitecture.di

import com.androidautharchitecture.data.payment.sdk.RazorpayPaymentGateway
import com.androidautharchitecture.domain.payment.gateway.PaymentGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    @Singleton
    abstract fun bindPaymentGateway(
        impl: RazorpayPaymentGateway
    ): PaymentGateway
}
