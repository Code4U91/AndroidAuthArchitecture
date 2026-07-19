package com.androidautharchitecture.data.auth.repository

import com.androidautharchitecture.app.session.SessionManager
import com.androidautharchitecture.core.network.safeApiCall
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.data.auth.mapper.toDto
import com.androidautharchitecture.data.auth.mapper.toUserSession
import com.androidautharchitecture.data.auth.remote.api.AuthApi
import com.androidautharchitecture.data.auth.remote.dto.FacebookLoginRequestDto
import com.androidautharchitecture.data.auth.remote.dto.GoogleLoginRequestDto
import com.androidautharchitecture.data.auth.remote.dto.RefreshRequestDto
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

    override suspend fun loginWithGoogle(idToken: String): AppResult<UserSession> {
        return safeApiCall {
            val session = api
                .loginWithGoogle(GoogleLoginRequestDto(idToken))
                .toUserSession()
            sessionManager.createSession(session)
            session
        }

    }

    override suspend fun loginWithFacebook(accessToken: String): AppResult<UserSession> {
        return safeApiCall {
            val session = api
                .loginWithFacebook(FacebookLoginRequestDto(accessToken))
                .toUserSession()
            sessionManager.createSession(session)
            session
        }

    }

    override suspend fun refreshToken(refreshToken: String): AppResult<UserSession> {

        return safeApiCall {
            val session = api
                .refreshToken(RefreshRequestDto(refreshToken))
                .toUserSession()

            sessionManager.createSession(session)

            session
        }
    }

    override suspend fun perform401ErrorCall(): AppResult<Unit> {
        return safeApiCall {
            api.force401Error()
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
