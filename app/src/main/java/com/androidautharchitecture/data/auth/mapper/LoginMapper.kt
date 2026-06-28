package com.androidautharchitecture.data.auth.mapper

import com.androidautharchitecture.data.auth.remote.dto.LoginRequestDto
import com.androidautharchitecture.data.auth.remote.dto.LoginResponseDto
import com.androidautharchitecture.domain.auth.model.LoginCredentials
import com.androidautharchitecture.domain.auth.model.UserSession

fun LoginCredentials.toDto() = LoginRequestDto(
    email = email,
    password = password
)

fun LoginResponseDto.toUserSession(): UserSession {

    return UserSession(
        userId = userId,
        accessToken = accessToken,
        expiresAt = expireAt.toString()
    )
}