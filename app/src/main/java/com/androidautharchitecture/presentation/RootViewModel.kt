package com.androidautharchitecture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidautharchitecture.app.session.SessionProvider
import com.androidautharchitecture.domain.auth.usecase.RestoreSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    restoreSession: RestoreSessionUseCase,
    sessionProvider: SessionProvider
) : ViewModel() {

    val isSessionExists = sessionProvider.session
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            restoreSession()
        }
    }
}
