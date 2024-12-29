package com.cofee4you.utils

import java.security.MessageDigest

import java.security.SecureRandom
import android.util.Base64

object SecurityUtils {

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT).trim()
    }

    fun hashPassword(password: String, salt: String): String {
        if (password.isEmpty()) {
            throw IllegalArgumentException("Password cannot be empty")
        }

        val combined = password + salt
        val bytes = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
