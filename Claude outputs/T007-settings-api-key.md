# T007 · Settings — API key configurable por usuario

Estado: `[ ]` pendiente
Fase: 1 — Motor Google SpeechRecognizer
Depende de: T006-fix4 ✅

## Objetivo
El usuario introduce su propia API key de OpenAI en una pantalla de ajustes del teclado.
La key se guarda cifrada en el dispositivo. Ningún APK lleva tokens hardcoded.

## Archivos a crear/modificar

---

### 1. Añadir dependencia en `app/build.gradle.kts`

```kotlin
dependencies {
    // ...existentes...
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

---

### 2. Crear `app/src/main/java/com/yolatam/teclado/ApiKeyManager.kt`

```kotlin
package com.yolatam.teclado

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {

    private const val PREFS_FILE = "voiceime_secure_prefs"
    private const val KEY_OPENAI  = "openai_api_key"

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
```

---

### 3. Crear `app/src/main/java/com/yolatam/teclado/SettingsActivity.kt`

```kotlin
package com.yolatam.teclado

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etKey = findViewById<EditText>(R.id.etApiKey)
        val btnSave = findViewById<Button>(R.id.btnSaveKey)

        // Mostrar key actual (enmascarada)
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
```

---

### 4. Crear `app/src/main/res/layout/activity_settings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:background="#1C1C1E">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="VoiceIME Settings"
        android:textColor="#FFFFFF"
        android:textSize="22sp"
        android:textStyle="bold"
        android:layout_marginBottom="8dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Your key is stored encrypted on this device only.\nGet yours at platform.openai.com/api-keys"
        android:textColor="#8E8E93"
        android:textSize="13sp"
        android:layout_marginBottom="24dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="OpenAI API Key"
        android:textColor="#FFFFFF"
        android:textSize="14sp"
        android:layout_marginBottom="8dp" />

    <EditText
        android:id="@+id/etApiKey"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="sk-..."
        android:textColorHint="#636366"
        android:textColor="#FFFFFF"
        android:background="#2C2C2E"
        android:padding="12dp"
        android:inputType="textPassword"
        android:fontFamily="monospace"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnSaveKey"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Save"
        android:textColor="#FFFFFF"
        android:backgroundTint="#4CAF50" />

</LinearLayout>
```

---

### 5. Declarar en `AndroidManifest.xml`

Dentro de `<application>`, añadir:
```xml
<activity
    android:name=".SettingsActivity"
    android:exported="true"
    android:label="VoiceIME Settings">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

---

### 6. Modificar `TecladoIME.kt`

#### 6a. En `onCreate()`, reemplazar la inicialización del engine:

Antes:
```kotlin
suggestionEngine = OpenAISuggestionEngine(BuildConfig.OPENAI_API_KEY)
```

Después:
```kotlin
val apiKey = ApiKeyManager.getOpenAiKey(this)
suggestionEngine = OpenAISuggestionEngine(apiKey)
```

#### 6b. En el `onResult` del mic, ANTES de `mostrarEstado(PanelEstado.PREPARANDO)`, añadir guardia:

```kotlin
onResult = { crudo ->
    val key = ApiKeyManager.getOpenAiKey(this@TecladoIME)
    if (key.isBlank()) {
        Log.e("TecladoIME", "Sin API key configurada")
        mostrarEstado(PanelEstado.REPOSO)
        // Opcional: mostrar Toast avisando al usuario
        android.widget.Toast.makeText(
            this@TecladoIME,
            "Configura tu API key en ajustes de VoiceIME",
            android.widget.Toast.LENGTH_LONG
        ).show()
        return@startListening
    }
    // Reconstruir engine con la key actual (por si cambió en settings)
    suggestionEngine = OpenAISuggestionEngine(key)
    textoCrudo = crudo
    mostrarEstado(PanelEstado.PREPARANDO)
    servicioScope.launch {
        // ...resto igual
    }
}
```

---

### 7. Añadir botón de ajustes en `keyboard_view.xml` (estado REPOSO)

En el `estadoReposo` LinearLayout, añadir un botón pequeño de engranaje debajo del mic:

```xml
<ImageButton
    android:id="@+id/btnSettings"
    android:layout_width="32dp"
    android:layout_height="32dp"
    android:src="@android:drawable/ic_menu_preferences"
    android:background="?attr/selectableItemBackgroundBorderless"
    android:tint="#636366"
    android:contentDescription="Settings"
    android:layout_marginTop="16dp" />
```

Y en `TecladoIME.kt`, en `onCreateInputView()`:

```kotlin
view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
    val intent = Intent(this, SettingsActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
```

---

## Verificación
1. Build + install
2. Pulsar ⚙️ en el teclado → abre SettingsActivity ✓
3. Pegar API key → "API key guardada ✓" ✓
4. Volver al teclado → dictar → sugerencias aparecen ✓
5. Sin API key configurada → Toast de aviso en vez de crash ✓

## Commit
`feat(fase-1): T007 — settings con API key cifrada por usuario (EncryptedSharedPreferences)`
