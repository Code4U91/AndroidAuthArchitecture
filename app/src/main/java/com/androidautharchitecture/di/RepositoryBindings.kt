package com.androidautharchitecture.di

import com.androidautharchitecture.data.auth.repository.FakeAuthRepository
import com.androidautharchitecture.data.payment.repository.PaymentRepositoryImpl
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import com.androidautharchitecture.domain.payment.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        // Switch this binding back to AuthRepositoryImpl when integrating the real backend.
        impl: FakeAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        impl: PaymentRepositoryImpl
    ): PaymentRepository
}
