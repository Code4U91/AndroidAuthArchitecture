package com.androidautharchitecture.data.auth.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequestDto(
    val idToken: String
)
