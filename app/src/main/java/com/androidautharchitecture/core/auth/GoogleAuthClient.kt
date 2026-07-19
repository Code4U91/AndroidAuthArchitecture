package com.androidautharchitecture.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.androidautharchitecture.BuildConfig
import com.androidautharchitecture.domain.auth.manager.GoogleAuthManager
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : GoogleAuthManager {
    private val credentialManager = CredentialManager.create(context)

    override suspend fun getGoogleIdToken(activityContext: Context): String? {

        Timber.d("Using Client ID: ${BuildConfig.GOOGLE_CLIENT_ID}")
        val googleIdOption = GetSignInWithGoogleOption.Builder(
            serverClientId = BuildConfig.GOOGLE_CLIENT_ID
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )
            handleSignInResult(result)
        } catch (e: GetCredentialException) {
            Timber.e(e, "Google Sign-In failed")
            null
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse): String? {
        val credential = result.credential
        return if (credential is GoogleIdTokenCredential) {
            credential.idToken
        } else if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            googleIdTokenCredential.idToken
        } else {
            null
        }
    }
}
