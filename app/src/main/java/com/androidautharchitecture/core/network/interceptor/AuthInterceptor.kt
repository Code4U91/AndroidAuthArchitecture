package com.androidautharchitecture.core.network.interceptor

import com.androidautharchitecture.app.session.SessionProvider
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
        val token = sessionProvider.session.value
            ?.accessToken

        if(token == null){
            return chain.proceed(request)
        }

        val authenticateRequest = request.newBuilder()
            .header(
                "Authorization",
                "Bearer $token"
            )
            .build()

        return chain.proceed(authenticateRequest)
    }
}