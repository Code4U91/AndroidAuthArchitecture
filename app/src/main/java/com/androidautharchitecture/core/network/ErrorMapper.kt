package com.androidautharchitecture.core.network

import com.androidautharchitecture.core.result.AppError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.toAppError(): AppError {

    return when (this) {

        is SocketTimeoutException ->
            AppError.Timeout

        is IOException ->
            AppError.Network

        is SerializationException ->
            AppError.Serialization

        is
        HttpException -> {

            when (code()) {

                401 -> AppError.Unauthorized

                else -> AppError.Api(
                    code = code(),
                    message = message()
                )
            }
        }

        else ->
            AppError.Unknown
    }
}