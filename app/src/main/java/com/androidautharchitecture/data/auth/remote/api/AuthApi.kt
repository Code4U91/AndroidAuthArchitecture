package com.androidautharchitecture.data.auth.remote.api

import com.androidautharchitecture.data.auth.remote.dto.LoginRequestDto
import com.androidautharchitecture.data.auth.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto
}