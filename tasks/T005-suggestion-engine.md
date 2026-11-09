# T005 · SuggestionEngine — LLM → 3 versiones mejoradas

Estado: `[x]` completada
Fase: 1 — Motor Google SpeechRecognizer
Depende de: T004 ✅

## Contexto
Ver ADR-008. Recibe texto crudo de T004 y genera 3 versiones mejoradas
llamando a la API de OpenAI (o Anthropic). El resultado alimenta la UI del T006.

## Objetivo
Dado un texto crudo transcrito ("hola que tal como estas quedamos esta tarde"),
devolver 3 versiones con puntuación correcta y estilos distintos.

## Archivos a crear

### `app/src/main/kotlin/com/yolatam/teclado/SuggestionEngine.kt`
```kotlin
package com.yolatam.teclado

data class Sugerencias(
    val original: String,
    val v1: String,  // neutro con puntuación
    val v2: String,  // informal/expresivo
    val v3: String   // compacto
)

interface SuggestionEngine {
    suspend fun generar(textoCrudo: String): Sugerencias
}
```

### `app/src/main/kotlin/com/yolatam/teclado/OpenAISuggestionEngine.kt`
Usar OkHttp (ya en el proyecto o añadir a build.gradle.kts):
```kotlin
package com.yolatam.teclado

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import android.util.Log

class OpenAISuggestionEngine(private val apiKey: String) : SuggestionEngine {

    private val client = OkHttpClient()
    private val TAG = "TecladoIME"

    override suspend fun generar(textoCrudo: String): Sugerencias = withContext(Dispatchers.IO) {
        val prompt = """
            Eres un asistente de escritura. Recibirás texto dictado por voz (sin puntuación).
            Devuelve EXACTAMENTE este JSON (sin markdown, sin explicaciones):
            {
              "v1": "versión con puntuación correcta, tono neutro",
              "v2": "versión informal y expresiva, con signos de exclamación si aplica",
              "v3": "versión compacta, sin palabras de relleno"
            }
            Texto: $textoCrudo
        """.trimIndent()

        val body = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("messages", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
        }.toString()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Respuesta vacía")
        Log.d(TAG, "Respuesta API: $responseBody")

        val content = JSONObject(responseBody)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")

        val json = JSONObject(content)
        Sugerencias(
            original = textoCrudo,
            v1 = json.getString("v1"),
            v2 = json.getString("v2"),
            v3 = json.getString("v3")
        )
    }
}
```

### Añadir a `app/build.gradle.kts` (si no están):
```kotlin
dependencies {
    // ...existentes...
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### Añadir a `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## API Key
Por ahora: constante en `BuildConfig` vía `local.properties` (NO hardcoded en código).

En `local.properties` (no subir a git):
```
OPENAI_API_KEY=sk-...
```

En `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")
    }
}
```

Uso: `BuildConfig.OPENAI_API_KEY`

## Verificación
En `TecladoIME.kt`, tras recibir transcripción de T004:
```kotlin
val engine = OpenAISuggestionEngine(BuildConfig.OPENAI_API_KEY)
lifecycleScope.launch {
    val sugerencias = engine.generar(textoCrudo)
    Log.d("TecladoIME", "v1: ${sugerencias.v1}")
    Log.d("TecladoIME", "v2: ${sugerencias.v2}")
    Log.d("TecladoIME", "v3: ${sugerencias.v3}")
}
```

## Commit
`feat(fase-1): añadir SuggestionEngine con OpenAI gpt-4o-mini (T005)`


Nota ejecutor: build OK, clave en local.properties/BuildConfig. Verificacion API se hace en la prueba E2E de T006.
