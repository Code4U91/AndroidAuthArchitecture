package com.androidautharchitecture.data.auth.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FacebookLoginRequestDto(
    val accessToken: String
)
