package com.androidautharchitecture.core.network.interceptor

import com.androidautharchitecture.BuildConfig
import com.androidautharchitecture.app.session.SessionProvider
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionProvider: SessionProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        // SECURITY: Only attach the token to our own API domain.
        // This prevents leaking tokens to 3rd party services (Analytics, Maps, etc.)
        if (!isAllowedHost(request.url.host)) {
            return chain.proceed(request)
        }

        val token = sessionProvider.session.value
            ?.accessToken

        if (token == null) {
            return chain.proceed(request)
        }

        // Check if the Authorization header is already present and correct.
        // This prevents overwriting the new token if this is a retried request from TokenAuthenticator.
        val existingHeader = request.header("Authorization")
        if (existingHeader == "Bearer $token") {
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder()
            .header(
                "Authorization",
                "Bearer $token"
            )
            .build()

        return chain.proceed(authenticatedRequest)
    }

    private fun isAllowedHost(host: String): Boolean {
        val apiHost = BuildConfig.BASE_URL.toHttpUrlOrNull()?.host
        
        // For this template, we also allow google.com for the 401 test endpoint
        return host == apiHost || host == "www.google.com"
    }
}