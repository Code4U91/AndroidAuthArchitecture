package com.androidautharchitecture.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.androidautharchitecture.domain.security.CryptoService
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class AndroidKeystoreCryptoService @Inject constructor() : CryptoService {

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(CryptoConstants.TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val encryptedData = ByteBuffer
            .allocate(iv.size + encryptedBytes.size)
            .put(iv)
            .put(encryptedBytes)
            .array()

        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }

    override fun decrypt(cipherText: String): String {
        val encryptedData = Base64.decode(cipherText, Base64.NO_WRAP)
        val buffer = ByteBuffer.wrap(encryptedData)

        val iv = ByteArray(CryptoConstants.IV_LENGTH)
        buffer.get(iv)

        val encryptedBytes = ByteArray(buffer.remaining())
        buffer.get(encryptedBytes)

        val cipher = Cipher.getInstance(CryptoConstants.TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            GCMParameterSpec(CryptoConstants.AUTH_TAG_LENGTH, iv)
        )

        val decrypted = cipher.doFinal(encryptedBytes)
        return String(decrypted, Charsets.UTF_8)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(CryptoConstants.KEYSTORE).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(CryptoConstants.KEY_ALIAS, null) as? SecretKey
        return existingKey ?: createSecretKey()
    }

    private fun createSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            CryptoConstants.KEYSTORE
        )

        val parameterSpec = KeyGenParameterSpec.Builder(
            CryptoConstants.KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }
}
