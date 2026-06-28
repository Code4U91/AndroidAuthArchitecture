package com.androidautharchitecture.di

import com.androidautharchitecture.data.auth.local.DataStoreSessionStorage
import com.androidautharchitecture.data.auth.local.SessionStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindSessionStorage(
        impl : DataStoreSessionStorage
    ): SessionStorage
}