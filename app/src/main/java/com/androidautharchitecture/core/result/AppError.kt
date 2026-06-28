package com.androidautharchitecture.core.result

sealed interface AppError {

    data object Network: AppError
    data object Unauthorized: AppError
    data object Timeout: AppError
    data object Serialization: AppError
    data object Unknown: AppError

    data class Api(
        val code: Int,
        val message: String?
    ): AppError
}