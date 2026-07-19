package com.androidautharchitecture.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidautharchitecture.domain.auth.manager.FacebookAuthManager
import com.androidautharchitecture.domain.auth.manager.GoogleAuthManager
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.usecase.FacebookLoginUseCase
import com.androidautharchitecture.domain.auth.usecase.GoogleLoginUseCase
import com.androidautharchitecture.domain.auth.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val googleAuthManager: GoogleAuthManager,
    private val facebookLoginUseCase: FacebookLoginUseCase,
    private val facebookAuthManager: FacebookAuthManager,
) : ViewModel() {

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginUseCase(LoginCredentials(username, password))
        }
    }

    fun loginWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            val idToken = googleAuthManager.getGoogleIdToken(activityContext)
            if (idToken != null) {
                googleLoginUseCase(idToken)
            }
        }
    }

    fun loginWithFacebook(activityContext: Context) {
        viewModelScope.launch {
            val accessToken = facebookAuthManager.login(activityContext)
            if (accessToken != null) {
                facebookLoginUseCase(accessToken)
            }
        }
    }
}
