package com.androidautharchitecture.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class AndroidKeystoreCryptoService @Inject constructor() : CryptoService {

    /**
     * Encrypts the supplied text using AES/GCM.
     *
     * The returned value is Base64 encoded and contains:
     *
     * [12-byte IV][Encrypted Bytes]
     *
     * The IV is prefixed to the ciphertext so it can be extracted during
     * decryption. A new random IV is generated for every encryption.
     */
    override fun encrypt(plainText: String): String {

        val cipher = Cipher.getInstance(
            CryptoConstants.TRANSFORMATION
        )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getSecretKey()
        )

        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(
            plainText.toByteArray(Charsets.UTF_8)
        )

        val encryptedData = ByteBuffer
            .allocate(iv.size + encryptedBytes.size)
            .put(iv)
            .put(encryptedBytes)
            .array()

        return Base64.encodeToString(
            encryptedData,
            Base64.NO_WRAP
        )
    }

    /**
     * Decrypts a Base64 encoded string previously produced by [encrypt].
     *
     * The encoded value is expected to contain:
     *
     * [12-byte IV][Encrypted Bytes]
     */
    override fun decrypt(cipherText: String): String {

        val encryptedData = Base64.decode(
            cipherText,
            Base64.NO_WRAP
        )

        val buffer = ByteBuffer.wrap(encryptedData)

        val iv = ByteArray(CryptoConstants.IV_LENGTH)

        buffer.get(iv)

        val encryptedBytes = ByteArray(buffer.remaining())

        buffer.get(encryptedBytes)

        val cipher = Cipher.getInstance(
            CryptoConstants.TRANSFORMATION
        )

        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            GCMParameterSpec(
                CryptoConstants.AUTH_TAG_LENGTH,
                iv
            )
        )

        val decrypted = cipher.doFinal(
            encryptedBytes
        )

        return String(
            decrypted,
            Charsets.UTF_8
        )

    }

    /**
     * Returns the existing AES key stored in Android Keystore.
     * If the key doesn't exist yet, it will be created.
     */
    private fun getSecretKey(): SecretKey {

        val keyStore = KeyStore.getInstance(
            CryptoConstants.KEYSTORE
        ).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(
            CryptoConstants.KEY_ALIAS,
            null
        ) as? SecretKey

        return existingKey ?: createSecretKey()
    }

    /**
     * Creates and stores a new AES key inside Android Keystore.
     * This is called only once during the application's lifetime
     * (unless the key is removed by the system or the user).
     */
    private fun createSecretKey(): SecretKey {

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            CryptoConstants.KEYSTORE
        )

        val parameterSpec = KeyGenParameterSpec.Builder(
            CryptoConstants.KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(
                KeyProperties.BLOCK_MODE_GCM
            )
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(parameterSpec)

        return keyGenerator.generateKey()
    }

}