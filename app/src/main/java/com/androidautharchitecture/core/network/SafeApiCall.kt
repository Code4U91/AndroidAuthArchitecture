package com.androidautharchitecture.core.network


import com.androidautharchitecture.core.result.AppResult

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): AppResult<T> {

    return try {

        AppResult.Success(
            apiCall()
        )

    } catch (throwable: Throwable) {

        AppResult.Failure(
            throwable.toAppError()
        )
    }
}