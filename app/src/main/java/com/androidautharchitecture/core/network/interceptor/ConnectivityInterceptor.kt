package com.androidautharchitecture.core.network.interceptor

import com.androidautharchitecture.core.network.connectivity.NetworkMonitor
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkMonitor.isConnected()) {
            throw NoInternetException()
        }
        return chain.proceed(chain.request())
    }
}

class NoInternetException : IOException("No internet connection")
