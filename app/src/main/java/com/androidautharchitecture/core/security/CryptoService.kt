package com.androidautharchitecture.core.security

import kotlin.jvm.Throws

interface CryptoService {

    /**
     * Encrypts plain text and returns a Base64 encoded encrypted string.
     */
    @Throws(Exception::class)
    fun encrypt(plainText: String): String

    /**
     * Decrypts a previously encrypted Base64 encoded string.
     */
    @Throws(Exception::class)
    fun decrypt(cipherText: String): String
}