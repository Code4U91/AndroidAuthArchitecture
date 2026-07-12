package com.androidautharchitecture.domain.auth.repository

import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.model.UserSession

interface AuthRepository {

    suspend fun login(
        credentials: LoginCredentials
    ): AppResult<UserSession>

    suspend fun refreshToken(
        refreshToken: String
    ): AppResult<UserSession>

    suspend fun perform401ErrorCall(): AppResult<Unit>

    suspend fun logout(): AppResult<Unit>

    suspend fun restoreSession(): UserSession?
}