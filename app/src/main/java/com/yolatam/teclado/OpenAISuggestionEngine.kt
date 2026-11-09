package com.yolatam.teclado

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * SuggestionEngine que llama a la API de OpenAI (gpt-4o-mini).
 *
 * Convierte texto dictado sin puntuación en tres estilos de mensaje.
 * La API key llega por parámetro (BuildConfig desde local.properties),
 * nunca hardcodeada.
 */
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
            put("messages", JSONArray().apply {
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
