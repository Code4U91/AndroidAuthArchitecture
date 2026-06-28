package com.androidautharchitecture.domain.auth.usecase

import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        credentials: LoginCredentials
    ) = repository.login(credentials)
}