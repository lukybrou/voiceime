# Arquitectura — Teclado Android con dictado por IA

## Visión general

```
┌─────────────────────────────────────────────┐
│               TecladoIME                    │
│         (InputMethodService)                │
│                                             │
│   ┌──────────────┐    ┌──────────────────┐  │
│   │  KeyboardView│    │  MicButton       │  │
│   │  (teclado    │    │  (botón micro)   │  │
│   │   mínimo)    │    └────────┬─────────┘  │
│   └──────────────┘             │            │
└───────────────────────────────┼────────────┘
                                │ inicia
                    ┌───────────▼──────────┐
                    │  TranscriptionEngine │  ← interfaz
                    └───────────┬──────────┘
                                │
              ┌─────────────────┴──────────────┐
              │ Fase 1                Fase 2    │
   ┌──────────▼──────────┐   ┌────────────────▼──┐
   │ GoogleSpeechEngine  │   │ QwenAudioEngine    │
   │ (SpeechRecognizer)  │   │ (WebSocket + PCM)  │
   └─────────────────────┘   └────────────────────┘
```

## Componentes

| Clase | Descripción |
|---|---|
| `TecladoIME` | Servicio principal. Gestiona el ciclo de vida del IME y escribe texto con `InputConnection.commitText()` |
| `TranscriptionEngine` | Interfaz con dos métodos: `startListening()` y `stopListening()`. Devuelve texto por callback |
| `GoogleSpeechEngine` | Implementación Fase 1. Usa `SpeechRecognizer` de Android |
| `SettingsActivity` | Activity que pide el permiso de micrófono y, en Fase 2, guarda la clave API |
| `QwenAudioEngine` | (Fase 2, no implementado) WebSocket con audio PCM a 16 kHz hacia Alibaba |

## Paquete

`com.yolatam.teclado`

## Flujo de una transcripción (Fase 1)

1. Usuario abre app (ej. WhatsApp) y toca un campo de texto.
2. El sistema activa `TecladoIME`.
3. `TecladoIME` comprueba `EditorInfo.inputType`: si es contraseña, el botón queda gris.
4. Usuario pulsa el botón de micrófono.
5. `TecladoIME` llama a `GoogleSpeechEngine.startListening()`.
6. `SpeechRecognizer` captura la voz y devuelve el texto.
7. `TecladoIME` recibe el texto por callback y llama a `currentInputConnection.commitText(texto, 1)`.
8. El texto aparece en el campo de WhatsApp.
