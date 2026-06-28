package com.androidautharchitecture.app.session

import com.androidautharchitecture.domain.auth.model.UserSession

interface SessionUpdater : SessionProvider {

    fun update(session: UserSession?)
}