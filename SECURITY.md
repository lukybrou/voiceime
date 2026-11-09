# Política de seguridad · Security Policy

## ES

### Versiones soportadas

| Versión | Soporte |
|---------|---------|
| 0.1.x   | ✅ Activa |

### Reportar una vulnerabilidad

Si encuentras un problema de seguridad, **no abras un issue público**.

Usa el canal de [reporte privado de vulnerabilidades](https://github.com/lukybrou/voiceime/security/advisories/new) de GitHub o escribe a la dirección de contacto del repositorio.

Intentaremos responder en un plazo de 72 horas y publicar un parche lo antes posible.

### Notas de seguridad

- VoiceIME **no tiene servidor propio**, no hay telemetría ni analítica.
- Tu API key de OpenAI se guarda cifrada en tu dispositivo usando `EncryptedSharedPreferences` (AES256-GCM). Nunca sale del dispositivo salvo para autenticarte con la API de OpenAI.
- El APK **no incluye ningún token hardcoded**. Cada usuario usa su propia clave.
- El audio grabado se procesa localmente por Google SpeechRecognizer y nunca se envía a servidores de VoiceIME.

---

## EN

### Supported versions

| Version | Support |
|---------|---------|
| 0.1.x   | ✅ Active |

### Reporting a vulnerability

If you find a security issue, **do not open a public issue**.

Use GitHub's [private vulnerability reporting](https://github.com/lukybrou/voiceime/security/advisories/new) channel or contact the repository maintainer directly.

We aim to respond within 72 hours and release a patch as soon as possible.

### Security notes

- VoiceIME **has no backend server**, no telemetry and no analytics.
- Your OpenAI API key is stored encrypted on your device using `EncryptedSharedPreferences` (AES256-GCM). It never leaves the device except to authenticate with the OpenAI API.
- The APK **contains no hardcoded token**. Every user brings their own key.
- Recorded audio is processed locally by Google SpeechRecognizer and is never sent to VoiceIME servers.
