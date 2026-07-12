package com.androidautharchitecture.data.auth.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class LoginResponseDto(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val expireAt: Long
)