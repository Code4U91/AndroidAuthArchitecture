package com.androidautharchitecture.data.auth.local

import com.androidautharchitecture.domain.auth.model.UserSession

interface SessionStorage {

    suspend fun setSession(session: UserSession)

    suspend fun getSession(): UserSession?

    suspend fun clearSession()
}