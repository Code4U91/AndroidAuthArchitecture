package com.androidautharchitecture.core.security

internal object CryptoConstants {

    const val KEYSTORE = "AndroidKeyStore"

    const val KEY_ALIAS = "com.androidautharchitecture.auth.session"

    const val TRANSFORMATION = "AES/GCM/NoPadding"

    const val AES_MODE = "AES"

    const val IV_LENGTH = 12

    const val AUTH_TAG_LENGTH = 128

}