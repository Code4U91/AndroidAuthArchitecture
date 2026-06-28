package com.androidautharchitecture.app.session

import com.androidautharchitecture.data.auth.local.SessionStorage
import com.androidautharchitecture.domain.auth.model.UserSession
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
 class SessionManager @Inject constructor(
    private val storage: SessionStorage,
    private val updater: SessionUpdater
) {

    suspend fun restoreSession(): UserSession? {

        val session = storage.getSession()

        updater.update(session)

        return session
    }

    suspend fun createSession(session: UserSession) {

        storage.setSession(session)
        updater.update(session)
    }

    suspend fun clearSession() {

        storage.clearSession()
        updater.update(null)
    }

}