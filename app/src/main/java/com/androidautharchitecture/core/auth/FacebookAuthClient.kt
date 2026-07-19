package com.androidautharchitecture.core.auth

import android.app.Activity
import android.content.Context
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
 * Lives in the Core/Framework layer.
 *
 * How it works:
 * 1. [loginManager] handles the UI flow (launching the Facebook App or Webview).
 * 2. [callbackManager] handles the response from the Facebook Activity.
 * 3. [suspendCancellableCoroutine] bridges the SDK's callback-based API to a modern Kotlin Coroutine API.
 */
@Singleton
class FacebookAuthClient @Inject constructor() : FacebookAuthManager {

    // Internal Facebook SDK manager that parses results from Activity results
    private val callbackManager = CallbackManager.Factory.create()
    
    // Main entry point for Facebook Login actions
    private val loginManager = LoginManager.getInstance()

    override suspend fun login(activityContext: Context): String? {
        val activity = activityContext as? Activity ?: return null

        return suspendCancellableCoroutine { continuation ->
            val callback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // Clean up: Unregister to avoid memory leaks or duplicate calls
                    loginManager.unregisterCallback(callbackManager)
                    if (!continuation.isCompleted) {
                        // Return the raw access token string to the caller
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

            // Register the callback before starting the login flow
            loginManager.registerCallback(callbackManager, callback)

            // Trigger the Facebook UI. Uses 'public_profile' and 'email' as default scopes.
            loginManager.logInWithReadPermissions(
                activity,
                listOf("email", "public_profile")
            )

            // Ensure we unregister if the coroutine is cancelled by the ViewModel/caller
            continuation.invokeOnCancellation {
                loginManager.unregisterCallback(callbackManager)
            }
        }
    }

    override fun logout() {
        // Clears the Facebook SDK's internal AccessToken cache
        loginManager.logOut()
    }

    /**
     * This method must be called from the Activity's onActivityResult.
     * It passes the Intent data to the Facebook [callbackManager], which then 
     * triggers the [FacebookCallback] defined in the login method.
     */
    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
