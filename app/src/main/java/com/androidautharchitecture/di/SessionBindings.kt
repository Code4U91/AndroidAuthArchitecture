package com.androidautharchitecture.di

import com.androidautharchitecture.app.session.DefaultSessionProvider
import com.androidautharchitecture.app.session.SessionUpdater
import com.androidautharchitecture.app.session.SessionProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionBindings {

    @Binds
    @Singleton
    abstract fun bindSessionProvider(
        impl: DefaultSessionProvider
    ) : SessionProvider

    @Binds
    @Singleton
    abstract fun bindMutableSessionProvider(
        impl: DefaultSessionProvider
    ): SessionUpdater
}