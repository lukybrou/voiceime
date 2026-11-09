# Política de privacidad · Privacy Policy

## ES

VoiceIME es una aplicación de teclado Android de código abierto. No tiene servidor propio, no recopila datos y no incluye telemetría ni analítica de ningún tipo.

### Qué datos salen de tu dispositivo

Solo el texto transcrito por el micrófono, y únicamente cuando pulsas el botón de micrófono:

| Destino | Dato | Para qué |
|---------|------|----------|
| `api.openai.com` | Texto transcrito | Generar sugerencias mejoradas |

No hay ninguna otra conexión. VoiceIME no envía audio, no accede a tu portapapeles, no lee otras aplicaciones ni recopila información de uso.

### Qué se guarda en tu dispositivo

- Tu API key de OpenAI, cifrada con `EncryptedSharedPreferences` (AES256-GCM). Solo tú puedes acceder a ella.
- Nada más.

### Permisos

| Permiso | Para qué |
|---------|----------|
| `RECORD_AUDIO` | Grabar tu voz cuando pulsas el micrófono |
| `INTERNET` | Enviar el texto transcrito a la API de OpenAI |

### Tu API key

Introduces tu propia clave de OpenAI en los ajustes del teclado. VoiceIME la guarda cifrada en tu dispositivo y la usa exclusivamente para llamar a `api.openai.com`. Nunca se envía a ningún servidor de VoiceIME (que no existe).

### Contacto

Abre un [issue](https://github.com/lukybrou/voiceime/issues) en el repositorio.

---

## EN

VoiceIME is an open-source Android keyboard app. It has no backend server, collects no data, and includes no telemetry or analytics of any kind.

### What data leaves your device

Only the text transcribed from the microphone, and only when you press the mic button:

| Destination | Data | Purpose |
|-------------|------|---------|
| `api.openai.com` | Transcribed text | Generate improved suggestions |

There are no other connections. VoiceIME does not send audio, does not access your clipboard, does not read other apps, and collects no usage information.

### What is stored on your device

- Your OpenAI API key, encrypted with `EncryptedSharedPreferences` (AES256-GCM). Only you can access it.
- Nothing else.

### Permissions

| Permission | Purpose |
|------------|---------|
| `RECORD_AUDIO` | Record your voice when you press the mic button |
| `INTERNET` | Send transcribed text to the OpenAI API |

### Your API key

You enter your own OpenAI key in the keyboard settings. VoiceIME stores it encrypted on your device and uses it exclusively to call `api.openai.com`. It is never sent to any VoiceIME server (which does not exist).

### Contact

Open an [issue](https://github.com/lukybrou/voiceime/issues) in the repository.
