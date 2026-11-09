package com.yolatam.teclado

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/**
 * Pantalla de ajustes del teclado (ADR-005).
 *
 * Permite al usuario introducir y guardar su API key de OpenAI, que se
 * almacena cifrada con ApiKeyManager. Se abre desde el botón ⚙️ del teclado.
 */
class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etKey = findViewById<EditText>(R.id.etApiKey)
        val btnSave = findViewById<Button>(R.id.btnSaveKey)

        val current = ApiKeyManager.getOpenAiKey(this)
        if (current.isNotBlank()) {
            etKey.setText(current)
        }

        btnSave.setOnClickListener {
            val key = etKey.text.toString().trim()
            if (key.startsWith("sk-") && key.length > 20) {
                ApiKeyManager.saveOpenAiKey(this, key)
                Toast.makeText(this, "API key guardada ✓", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Introduce una API key válida (empieza por sk-)", Toast.LENGTH_LONG).show()
            }
        }
    }
}
