package com.androidautharchitecture.core.network

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.app.session.SessionProvider
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import dagger.Lazy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionProvider: SessionProvider,
    private val sessionManager: SessionManager,
    private val authRepository: Lazy<AuthRepository> // resolving circular dependency
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        Timber.tag("AuthTest").d("Authenticator triggered for 401")

        synchronized(this) {
            val session = runBlocking { sessionProvider.session.firstOrNull() }
            val currentToken = session?.accessToken

            val authHeader = response.request.header("Authorization")
            val requestToken = authHeader?.removePrefix("Bearer ")?.trim()

            Timber.tag("AuthTest").d("Current: $currentToken, Request: $requestToken")

            if (currentToken != null && currentToken != requestToken) {

                Timber.tag("AuthTest").d("Token already refreshed by another thread, retrying...")

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = session?.refreshToken

            if (refreshToken == null) {
                Timber.tag("AuthTest").e("No refresh token available. Logging out.")
                runBlocking { sessionManager.clearSession() }
                return null
            }

            Timber.tag("AuthTest").d("Starting token refresh...")
            val refreshResult = runBlocking {
                authRepository.get().refreshToken(refreshToken)
            }

            return when (refreshResult) {
                is AppResult.Success -> {
                    Timber.tag("AuthTest").d("Refresh SUCCESS. Retrying request with new token.")
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResult.data.accessToken}")
                        .build()
                }
                is AppResult.Failure -> {
                    Timber.tag("AuthTest").e("Refresh FAILED. Logging out.")
                    runBlocking { sessionManager.clearSession() }
                    null
                }
                else -> null
            }
        }
    }
}
