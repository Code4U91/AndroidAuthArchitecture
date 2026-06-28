package com.androidautharchitecture.core.network.interceptor

import com.androidautharchitecture.app.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnauthorizedInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())

        val hasAuthorizationHeader = chain.request()
            .header("Authorization") != null

        if (response.code == 401 && hasAuthorizationHeader) {
            scope.launch {
                sessionManager.clearSession()
            }
        }
        return response
    }
}