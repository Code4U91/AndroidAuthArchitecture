package com.androidautharchitecture.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import com.androidautharchitecture.domain.auth.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// BASIC TEST VM - for testing 401 network error and logout use case

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository // directly using repo for testing only
) : ViewModel() {

    private val _testResult = MutableStateFlow("")
    val testResult = _testResult.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun trigger401Test() {
        viewModelScope.launch {
            _testResult.value = "Calling endpoint that returns 401..."
            
            // This call will fail with 401, which will trigger TokenAuthenticator.
            // If the refresh succeeds, TokenAuthenticator will RETRY this call.
            val result = authRepository.perform401ErrorCall()
            
            _testResult.value = when (result) {
                is AppResult.Success -> "Success! (This shouldn't happen for a 401 endpoint)"
                is AppResult.Failure -> "Final Result: ${result.error}"
                else -> "Unknown"
            }
        }
    }
}
