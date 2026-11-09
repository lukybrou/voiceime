package com.yolatam.teclado

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Guarda la API key de OpenAI cifrada en el dispositivo (AES256-GCM).
 *
 * Existe para que ningún APK público lleve tokens hardcoded (T007):
 * cada usuario introduce su propia key desde SettingsActivity.
 */
object ApiKeyManager {

    private const val PREFS_FILE = "voiceime_secure_prefs"
    private const val KEY_OPENAI = "openai_api_key"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getOpenAiKey(context: Context): String =
        prefs(context).getString(KEY_OPENAI, "") ?: ""

    fun saveOpenAiKey(context: Context, key: String) =
        prefs(context).edit().putString(KEY_OPENAI, key).apply()

    fun hasKey(context: Context): Boolean =
        getOpenAiKey(context).isNotBlank()
}
