package com.androidautharchitecture.di

import com.androidautharchitecture.data.security.AndroidKeystoreCryptoService
import com.androidautharchitecture.domain.security.CryptoService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindCryptoService(
        impl: AndroidKeystoreCryptoService
    ): CryptoService
}
