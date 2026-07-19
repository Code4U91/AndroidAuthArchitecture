package com.androidautharchitecture.core.network

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.app.session.SessionProvider
import com.androidautharchitecture.data.auth.remote.api.AuthApi
import com.androidautharchitecture.data.auth.remote.dto.RefreshRequestDto
import com.androidautharchitecture.data.auth.mapper.toUserSession
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
    private val authApi: Lazy<AuthApi> // Inject the API directly to break the cycle
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        // Avoid infinite loops: if we've already tried more than twice, stop.
        if (responseCount(response) >= 3) {
            Timber.tag("AuthTest").e("Too many 401 retries. Logging out.")
            runBlocking { sessionManager.clearSession() }
            return null
        }

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
            
            // Call the API directly using safeApiCall logic (or simplified for Authenticator)
            return try {
                val refreshResponse = runBlocking {
                    authApi.get().refreshToken(RefreshRequestDto(refreshToken))
                }
                val newSession = refreshResponse.toUserSession()
                runBlocking { sessionManager.createSession(newSession) }

                Timber.tag("AuthTest").d("Refresh SUCCESS. Retrying request with new token.")
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newSession.accessToken}")
                    .build()
            } catch (e: Exception) {
                Timber.tag("AuthTest").e(e, "Refresh FAILED. Logging out.")
                runBlocking { sessionManager.clearSession() }
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var rs = response.priorResponse
        while (rs != null) {
            result++
            rs = rs.priorResponse
        }
        return result
    }
}
