# Changelog

All notable changes to VoiceIME are documented here.

## [0.1.0] — 2026-11-09

### Added
- Push-to-talk voice dictation using Google SpeechRecognizer (es-ES)
- 3 AI-polished suggestions via OpenAI gpt-4o-mini
- 4-state UI: idle → recording → preparing → suggestions
- Encrypted API key storage per user (EncryptedSharedPreferences, AES256)
- Settings screen to enter and save your own OpenAI API key
- Gear button in keyboard panel to open Settings
- Green spinner "Preparando..." during LLM processing
- Insert selected suggestion into any text field
- Works in WhatsApp, Gmail, Telegram and any Android text input

### Architecture
- `TecladoIME` — main InputMethodService with 4-state panel (REPOSO, GRABANDO, PREPARANDO, SUGERENCIAS)
- `GoogleTranscriptionEngine` — Google Android SpeechRecognizer wrapper
- `OpenAISuggestionEngine` — OkHttp calls to OpenAI Chat Completions
- `ApiKeyManager` — encrypted key storage singleton
- `SettingsActivity` — standalone Activity for API key configuration

---

Detailed changes per fix in [commits](https://github.com/lukybrou/voiceime/commits/main).
