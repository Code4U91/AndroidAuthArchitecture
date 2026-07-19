package com.androidautharchitecture.domain.auth.usecase

import com.androidautharchitecture.core.result.AppResult
import com.androidautharchitecture.domain.auth.model.UserSession
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AppResult<UserSession> {
        return repository.loginWithGoogle(idToken)
    }
}
