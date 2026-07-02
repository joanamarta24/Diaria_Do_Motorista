package com.example.diaria_do_motorista.data.db.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveCredentials(email: String, password: String) {
        prefs.edit().apply {
            putString("email", email)
            putString("password", password)
            putBoolean("remember_me", true)
            apply()
        }
    }

    fun getCredentials(): Pair<String, String>? {
        val email = prefs.getString("email", null)
        val password = prefs.getString("password", null)
        return if (email != null && password != null) {
            email to password
        } else null
    }
    fun clearCredentials() {
        prefs.edit().apply {
            remove("email")
            remove("password")
            putBoolean("remember_me", false)
            apply()
        }
    }

    fun isRememberMe(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

}