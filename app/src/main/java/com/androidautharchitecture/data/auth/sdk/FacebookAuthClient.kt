package com.androidautharchitecture.data.auth.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.androidautharchitecture.domain.auth.manager.FacebookAuthManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Implementation of [FacebookAuthManager] using the official Facebook Android SDK.
 * Lives in the Data/Infrastructure layer.
 */
@Singleton
class FacebookAuthClient @Inject constructor() : FacebookAuthManager {

    private val callbackManager = CallbackManager.Factory.create()
    private val loginManager = LoginManager.getInstance()

    override suspend fun login(activityContext: Context): String? {
        val activity = activityContext as? Activity ?: return null

        return suspendCancellableCoroutine { continuation ->
            val callback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    loginManager.unregisterCallback(callbackManager)
                    if (!continuation.isCompleted) {
                        continuation.resume(result.accessToken.token)
                    }
                }

                override fun onCancel() {
                    loginManager.unregisterCallback(callbackManager)
                    if (!continuation.isCompleted) {
                        continuation.resume(null)
                    }
                }

                override fun onError(error: FacebookException) {
                    loginManager.unregisterCallback(callbackManager)
                    if (!continuation.isCompleted) {
                        continuation.resume(null)
                    }
                }
            }

            loginManager.registerCallback(callbackManager, callback)

            loginManager.logInWithReadPermissions(
                activity,
                listOf("email", "public_profile")
            )

            continuation.invokeOnCancellation {
                loginManager.unregisterCallback(callbackManager)
            }
        }
    }

    override fun logout() {
        loginManager.logOut()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
