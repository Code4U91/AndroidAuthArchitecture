package com.androidautharchitecture.data.auth.repository

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.core.network.safeApiCall
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.core.result.clearSessionIfUnauthorized
import com.androidautharchitecture.data.auth.mapper.toDto
import com.androidautharchitecture.data.auth.mapper.toUserSession
import com.androidautharchitecture.data.auth.remote.api.AuthApi
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.model.UserSession
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): AppResult<UserSession> {

        return safeApiCall {
            val session = api
                .login(credentials.toDto())
                .toUserSession()

            sessionManager.createSession(session)

            session
        }
    }

    override suspend fun logout(): AppResult<Unit> {

        return safeApiCall {
            sessionManager.clearSession()
        }
    }

    override suspend fun restoreSession(): UserSession? {
        return sessionManager.restoreSession()
    }
}