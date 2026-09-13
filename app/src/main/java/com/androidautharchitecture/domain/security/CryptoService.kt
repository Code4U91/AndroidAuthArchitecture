package com.androidautharchitecture.domain.security

import kotlin.jvm.Throws

/**
 * Pure Kotlin contract for data encryption and decryption.
 * Lives in Domain layer so data storage mechanism can stay decoupled from Android Keystore.
 */
interface CryptoService {

    @Throws(Exception::class)
    fun encrypt(plainText: String): String

    @Throws(Exception::class)
    fun decrypt(cipherText: String): String
}
