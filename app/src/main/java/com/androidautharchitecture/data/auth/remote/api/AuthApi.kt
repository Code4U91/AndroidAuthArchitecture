package com.androidautharchitecture.data.auth.remote.api

import com.androidautharchitecture.data.auth.remote.dto.LoginRequestDto
import com.androidautharchitecture.data.auth.remote.dto.LoginResponseDto
import com.androidautharchitecture.data.auth.remote.dto.RefreshRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("refresh")
    suspend fun refreshToken(
        @Body request: RefreshRequestDto
    ): LoginResponseDto

    // Using google.com because Network Interceptors need a real-looking host to trigger.
    // Our interceptor will catch this and turn it into a 401 locally.
    @GET
    suspend fun force401Error(@Url url: String = "https://www.google.com/force-401"): Unit
}