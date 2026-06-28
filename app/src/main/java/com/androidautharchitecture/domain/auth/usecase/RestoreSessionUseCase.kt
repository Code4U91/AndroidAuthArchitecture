package com.androidautharchitecture.domain.auth.usecase

import com.androidautharchitecture.domain.auth.repository.AuthRepository
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() = repository.restoreSession()
}