package com.androidautharchitecture.app.session

import com.androidautharchitecture.domain.auth.model.UserSession
import kotlinx.coroutines.flow.StateFlow

interface SessionProvider {

    val session: StateFlow<UserSession?>
}