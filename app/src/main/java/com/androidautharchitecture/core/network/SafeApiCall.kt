package com.androidautharchitecture.core.network


import com.androidautharchitecture.core.result.AppResult
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): AppResult<T> {

    return try {

        AppResult.Success(
            apiCall()
        )

    } catch (e: Exception) {

        when (e) {
            is IOException,
            is HttpException,
            is SerializationException -> AppResult.Failure(e.toAppError())

            else -> throw e
        }
    }
}