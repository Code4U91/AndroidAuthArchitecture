package com.androidautharchitecture.domain.auth.model

import kotlinx.serialization.Serializable

@Serializable  // not recommended to add this in the domain, but fine for this project
data class UserSession(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt:  String
)