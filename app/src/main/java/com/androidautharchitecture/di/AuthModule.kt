package com.androidautharchitecture.di

import com.androidautharchitecture.core.auth.FacebookAuthClient
import com.androidautharchitecture.core.auth.GoogleAuthClient
import com.androidautharchitecture.domain.auth.manager.FacebookAuthManager
import com.androidautharchitecture.domain.auth.manager.GoogleAuthManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthManager(
        impl: GoogleAuthClient
    ): GoogleAuthManager

    @Binds
    @Singleton
    abstract fun bindFacebookAuthManager(
        impl: FacebookAuthClient
    ): FacebookAuthManager
}
