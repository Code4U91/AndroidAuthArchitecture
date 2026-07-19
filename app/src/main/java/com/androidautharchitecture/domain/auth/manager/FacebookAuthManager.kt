package com.androidautharchitecture.domain.auth.manager

import android.content.Context

/**
 * Interface defining the contract for Facebook Authentication.
 * This lives in the Domain layer to keep the app business logic decoupled from the Facebook SDK.
 */
interface FacebookAuthManager {
    /**
     * Initiates the Facebook Login flow.
     * @param activityContext Must be an Activity context as the Facebook SDK needs to launch its own UI.
     * @return The Facebook Access Token string if successful, null otherwise.
     */
    suspend fun login(activityContext: Context): String?

    /**
     * Logs the user out of the Facebook SDK session.
     * This ensures that the next login attempt starts fresh and doesn't automatically 
     * use the previous user's cached Facebook token.
     */
    fun logout()

    /**
     * Bridge method to pass activity results back to the Facebook SDK's CallbackManager.
     * Required because Facebook SDK still relies on the legacy onActivityResult pattern.
     */
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?)
}
