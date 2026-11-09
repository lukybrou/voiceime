package com.yolatam.teclado

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Motor de transcripción de la Fase 1 basado en SpeechRecognizer de Android.
 *
 * Existe para cumplir ADR-002/ADR-003: transcripción local sin servidor,
 * oculta detrás de la interfaz TranscriptionEngine. Idioma es-ES fijo
 * (configurable en T007).
 */
class GoogleTranscriptionEngine(private val context: Context) : TranscriptionEngine {

    private var recognizer: SpeechRecognizer? = null
    private val TAG = "TecladoIME"

    override fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "SpeechRecognizer no disponible en el dispositivo")
            onError("Reconocimiento de voz no disponible")
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val texto = matches?.firstOrNull() ?: ""
                Log.d(TAG, "Transcripción: $texto")
                onResult(texto)
            }
            override fun onError(error: Int) {
                Log.e(TAG, "Error SpeechRecognizer: $error")
                onError("Error de transcripción ($error)")
            }
            override fun onReadyForSpeech(p: Bundle?) {
                Log.d(TAG, "Listo para escuchar")
            }
            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Inicio de voz detectado")
            }
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d(TAG, "Fin de voz detectado")
            }
            override fun onPartialResults(p: Bundle?) {
                val parciales = p?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                parciales?.firstOrNull()?.let { Log.d(TAG, "Parcial: $it") }
            }
            override fun onEvent(t: Int, p: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer?.startListening(intent)
        Log.d(TAG, "Escuchando...")
    }

    override fun stopListening() {
        recognizer?.stopListening()
    }

    override fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }
}
