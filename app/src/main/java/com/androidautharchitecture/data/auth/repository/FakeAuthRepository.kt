package com.androidautharchitecture.data.auth.repository

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.core.network.safeApiCall
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.data.auth.remote.api.AuthApi
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.model.UserSession
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAuthRepository @Inject constructor(
    private val sessionManager: SessionManager,
    private val api: AuthApi // for testing 401 error
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): AppResult<UserSession> {
        val session = UserSession(
            userId = "1",
            accessToken = "fake-jwt-token",
            refreshToken = "fake-refresh-token",
            expiresAt = (System.currentTimeMillis() + 24 * 60 * 60 * 1000).toString()
        )

        sessionManager.createSession(session)
        return AppResult.Success(session)
    }

    override suspend fun refreshToken(refreshToken: String): AppResult<UserSession> {
        val session = UserSession(
            userId = "1",
            accessToken = "new-fake-jwt-token-${System.currentTimeMillis()}",
            refreshToken = "new-fake-refresh-token-${System.currentTimeMillis()}",
            expiresAt = (System.currentTimeMillis() + 24 * 60 * 60 * 1000).toString()
        )

        Timber.tag("Refreshed").d("Ref called")

        sessionManager.createSession(session)
        return AppResult.Success(session)
    }

    override suspend fun perform401ErrorCall(): AppResult<Unit> {
        Timber.tag("perform401").d("error called")
        return safeApiCall {
            api.force401Error()
        }
    }

    override suspend fun logout(): AppResult<Unit> {
        sessionManager.clearSession()
        return AppResult.Success(Unit)
    }

    override suspend fun restoreSession(): UserSession? {
        return sessionManager.restoreSession()
    }
}
