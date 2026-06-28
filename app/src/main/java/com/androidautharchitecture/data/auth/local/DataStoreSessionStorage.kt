package com.androidautharchitecture.data.auth.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.androidautharchitecture.core.security.CryptoService
import com.androidautharchitecture.domain.auth.model.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class DataStoreSessionStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
    private val cryptoService: CryptoService
) : SessionStorage {

    companion object {
        private val SESSION_KEY = stringPreferencesKey("auth.session")
    }

    override suspend fun setSession(session: UserSession) {

        dataStore.edit { prefs ->

            val sessionJson = json.encodeToString(
                UserSession.serializer(),
                session
            )

            prefs[SESSION_KEY] = cryptoService.encrypt(sessionJson)

        }
    }

    override suspend fun getSession(): UserSession? {

        val prefs = dataStore.data.first()
        val encryptedSession = prefs[SESSION_KEY] ?: return null

        return try {

            val decryptedSession = cryptoService.decrypt(
                encryptedSession
            )

            json.decodeFromString(
                UserSession.serializer(),
                decryptedSession
            )

        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun clearSession() {

        dataStore.edit {
            it.remove(SESSION_KEY)
        }
    }
}