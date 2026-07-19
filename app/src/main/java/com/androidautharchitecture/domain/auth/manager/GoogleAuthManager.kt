package com.androidautharchitecture.domain.auth.manager

import android.content.Context

interface GoogleAuthManager {
    suspend fun getGoogleIdToken(activityContext: Context): String?
}
