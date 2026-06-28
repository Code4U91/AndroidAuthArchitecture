package com.androidautharchitecture.data.auth.repository

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.model.UserSession
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): AppResult<UserSession> {
        val session = UserSession(
            userId = "1",
            accessToken = "fake-jwt-token",
            expiresAt = (System.currentTimeMillis() + 24 * 60 * 60 * 1000).toString()
        )

        sessionManager.createSession(session)
        return AppResult.Success(session)
    }

    override suspend fun logout(): AppResult<Unit> {
        sessionManager.clearSession()
        return AppResult.Success(Unit)
    }

    override suspend fun restoreSession(): UserSession? {
        return sessionManager.restoreSession()
    }
}
