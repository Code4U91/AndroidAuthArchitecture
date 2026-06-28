package com.androidautharchitecture.app.session

import com.androidautharchitecture.domain.auth.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionProvider @Inject constructor() : SessionUpdater {

    private val _session: MutableStateFlow<UserSession?> = MutableStateFlow(null)

    override val session: StateFlow<UserSession?> = _session.asStateFlow()

    override fun update(
        session: UserSession?
    ) {
        _session.value = session
    }


}