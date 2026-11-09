# T004 · TranscriptionEngine — SpeechRecognizer → texto crudo

Estado: `[x]` completada
Fase: 1 — Motor Google SpeechRecognizer
Depende de: T003 ✅, T003-fix ✅

## Contexto
Ver ADR-008 para el flujo completo. Esta tarea implementa la capa de grabación y transcripción.
El resultado es texto crudo que T005 convertirá en sugerencias.

## Objetivo
Capturar audio con el micrófono mientras el usuario mantiene pulsado el botón,
transcribirlo con SpeechRecognizer y exponer el resultado como String.

## Archivos a crear/modificar

### Crear: `app/src/main/kotlin/com/yolatam/teclado/TranscriptionEngine.kt`
```kotlin
package com.yolatam.teclado

interface TranscriptionEngine {
    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)
    fun stopListening()
    fun destroy()
}
```

### Crear: `app/src/main/kotlin/com/yolatam/teclado/GoogleTranscriptionEngine.kt`
```kotlin
package com.yolatam.teclado

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class GoogleTranscriptionEngine(private val context: Context) : TranscriptionEngine {

    private var recognizer: SpeechRecognizer? = null
    private val TAG = "TecladoIME"

    override fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
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
            // Implementaciones vacías requeridas
            override fun onReadyForSpeech(p: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(p: Bundle?) {}
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
```

### Modificar: `AndroidManifest.xml`
Añadir dentro de `<manifest>` (antes de `<application>`):
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Modificar: `TecladoIME.kt`
- Instanciar `GoogleTranscriptionEngine` en `onCreate()`
- En el click del botón mic: llamar `transcriptionEngine.startListening(...)`
- Al recibir resultado: loguear y guardar en variable para T005
- Destruir en `onDestroy()`

Ejemplo de integración en el click listener:
```kotlin
btnMicrofono.setOnClickListener {
    Log.d("TecladoIME", "Micrófono pulsado")
    transcriptionEngine.startListening(
        onResult = { texto ->
            Log.d("TecladoIME", "Resultado: $texto")
            // T005 recibirá este texto
        },
        onError = { msg ->
            Log.e("TecladoIME", "Error: $msg")
        }
    )
}
```

## Verificación
```
adb logcat -s TecladoIME:D
```
Pulsar mic, hablar → debe aparecer:
```
D TecladoIME: Escuchando...
D TecladoIME: Transcripción: hola qué tal cómo estás
```

## Notas
- El permiso RECORD_AUDIO en un IME no requiere ActivityCompat.requestPermissions —
  el sistema lo solicita al activar el teclado si está en el manifest
- Si el reconocimiento falla con error 9 (INSUFFICIENT_PERMISSIONS): revocar y reinstalar APK
- Idioma es-ES hardcoded por ahora; configurable en T007 (Settings)

## Commit
`feat(fase-1): añadir TranscriptionEngine con Google SpeechRecognizer (T004)`


Nota ejecutor: build OK. Verificacion de voz se hace en la prueba E2E de T006 (logcat -s TecladoIME:D).
