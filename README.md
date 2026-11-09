# VoiceIME 🎙️

**ES** · Teclado Android con dictado por voz e IA · **EN** · Android keyboard with voice dictation and AI

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg?logo=android)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Release](https://img.shields.io/github/v/release/lukybrou/voiceime)](https://github.com/lukybrou/voiceime/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg?logo=kotlin)](https://kotlinlang.org)
[![OpenAI](https://img.shields.io/badge/OpenAI-gpt--4o--mini-412991.svg?logo=openai)](https://platform.openai.com)

---

## ES — ¿Qué es VoiceIME?

VoiceIME es un teclado Android de código abierto con dictado por voz e IA. Pulsa el micrófono, habla, y el teclado transcribe tu voz y te ofrece 3 versiones pulidas por GPT-4o-mini para elegir.

### Características

- 🎙️ **Dictado por voz** — Google SpeechRecognizer, español (es-ES)
- 🤖 **3 sugerencias IA** — versiones corregida, formal e informal vía OpenAI
- 🔐 **API key cifrada** — almacenada en el dispositivo con AES-256-GCM
- 📱 **Funciona en cualquier app** — WhatsApp, Gmail, Telegram, etc.
- 🔓 **Sin backend** — tu voz y tu clave nunca salen del dispositivo salvo a api.openai.com

### Instalación

1. Descarga `app-debug.apk` desde [Releases](https://github.com/lukybrou/voiceime/releases)
2. Activa **Instalar desde fuentes desconocidas** en tu Android
3. Instala el APK
4. Ve a **Ajustes → Sistema → Teclados → Gestionar teclados** → activa VoiceIME
5. En cualquier app, cambia el teclado a VoiceIME
6. Pulsa ⚙️ → introduce tu [OpenAI API key](https://platform.openai.com/api-keys)
7. Pulsa 🎙️ y habla

### Requisitos

- Android 8.0+ (API 26)
- API key de OpenAI: [platform.openai.com/api-keys](https://platform.openai.com/api-keys)

---

## EN — What is VoiceIME?

VoiceIME is an open-source Android keyboard with voice dictation and AI. Tap the microphone, speak, and the keyboard transcribes your voice and offers 3 AI-polished versions to pick from.

### Features

- 🎙️ **Voice dictation** — Google SpeechRecognizer, Spanish (es-ES)
- 🤖 **3 AI suggestions** — corrected, formal and informal versions via OpenAI
- 🔐 **Encrypted API key** — stored on-device with AES-256-GCM
- 📱 **Works in any app** — WhatsApp, Gmail, Telegram, and more
- 🔓 **No backend** — your voice and key never leave the device except to api.openai.com

### Installation

1. Download `app-debug.apk` from [Releases](https://github.com/lukybrou/voiceime/releases)
2. Enable **Install from unknown sources** on your Android device
3. Install the APK
4. Go to **Settings → System → Keyboards → Manage keyboards** → enable VoiceIME
5. In any app, switch your keyboard to VoiceIME
6. Tap ⚙️ → enter your [OpenAI API key](https://platform.openai.com/api-keys)
7. Tap 🎙️ and speak

### Requirements

- Android 8.0+ (API 26)
- OpenAI API key: [platform.openai.com/api-keys](https://platform.openai.com/api-keys)

---

## Documentación · Documentation

| Archivo · File | Contenido · Content |
|---|---|
| [`AGENTS.md`](AGENTS.md) | Reglas para agentes IA · AI agent rules |
| [`docs/DECISIONS.md`](docs/DECISIONS.md) | Decisiones de arquitectura · Architecture decisions |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Diagrama de componentes · Component diagram |
| [`CHANGELOG.md`](CHANGELOG.md) | Historial de versiones · Version history |
| [`CONTRIBUTING.md`](CONTRIBUTING.md) | Cómo contribuir · How to contribute |
| [`SECURITY.md`](SECURITY.md) | Reporte de vulnerabilidades · Vulnerability reporting |
| [`PRIVACY.md`](PRIVACY.md) | Política de privacidad · Privacy policy |

## Contribuir · Contributing

Lee [`CONTRIBUTING.md`](CONTRIBUTING.md) · Read [`CONTRIBUTING.md`](CONTRIBUTING.md)

## Licencia · License

[MIT](LICENSE) © [Douglas Ortega](https://github.com/lukybrou)
