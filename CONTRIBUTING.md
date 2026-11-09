# Contributing to VoiceIME

Thanks for your interest! VoiceIME is a small open-source project and contributions are welcome.

## Ways to contribute

- **Bug reports** — open an [issue](https://github.com/lukybrou/voiceime/issues) with your Android version, the keyboard version, and what happened vs. what you expected.
- **Feature ideas** — open an issue or start a [Discussion](https://github.com/lukybrou/voiceime/discussions) under Ideas.
- **Code** — fork the repo, make your change in a branch, and open a PR against `main`.
- **Languages** — VoiceIME currently only supports `es-ES`. If you want to add another language, open an issue first so we can discuss how to make the locale configurable.
- **LLM providers** — the engine interface (`SuggestionEngine`) is designed to be swapped. PRs adding Gemini, Groq or other providers are welcome.

## Development setup

1. Clone the repo and open in Android Studio (Ladybug or later).
2. Copy `local.properties.example` to `local.properties` — or just open Settings in the installed keyboard and enter your own OpenAI key. No token needs to be in the build.
3. Run on a physical device (IME keyboards don't work well on emulators).
4. `./gradlew assembleDebug` builds the APK.

## Code style

- Kotlin only, no Java.
- Keep the `PanelEstado` enum as the single source of truth for UI state.
- All LLM calls go through the `SuggestionEngine` interface — don't call OpenAI directly from `TecladoIME`.
- No hardcoded API tokens anywhere in the codebase.

## Pull request checklist

- [ ] `./gradlew assembleDebug` passes with no errors
- [ ] Tested on a physical Android device
- [ ] No API key or secret committed
- [ ] `CHANGELOG.md` updated under `[Unreleased]`

## License

By contributing you agree that your code will be released under the [MIT License](LICENSE).
