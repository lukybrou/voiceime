# Decisiones de arquitectura

Registro de decisiones tomadas y aceptadas. Lo que no aparece aquí sigue abierto.
El planificador es el único que puede añadir entradas; el ejecutor puede añadir
notas menores marcadas con `[EJECUTOR]`.

---

## ADR-001 — Teclado propio (IME) en lugar de extensión de Gboard

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

Google no permite añadir botones a la barra de Gboard. La única vía para
integrar un micrófono propio es implementar un teclado completo como IME
(InputMethodService).

---

## ADR-002 — Trabajo por fases

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

Fase 1: SpeechRecognizer de Android (sin servidor externo).  
Fase 2: Motor de IA externo (candidato principal: Alibaba Qwen-ASR).  
Fase 3: Pulido de texto (sin decidir).

---

## ADR-003 — Motor de transcripción detrás de una interfaz

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

El motor de transcripción se abstrae en la interfaz `TranscriptionEngine`.
La Fase 1 usa `GoogleSpeechEngine`. El cambio de motor en la Fase 2 no
debe requerir tocar `TecladoIME` ni la UI.

---

## ADR-004 — Texto inyectado con InputConnection.commitText()

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

Es la API estándar de Android para que un IME escriba en el campo activo,
incluido WhatsApp.

---

## ADR-005 — Permiso de micrófono pedido desde una Activity de ajustes

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

Un IME no puede pedir permisos en tiempo de ejecución por sí mismo.
Se añade `SettingsActivity` que el usuario abre desde los ajustes del sistema.

---

## ADR-006 — Micrófono desactivado en campos de contraseña

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

Si `EditorInfo.inputType` tiene variación de contraseña, el botón de
micrófono se deshabilita visualmente y no captura audio.

---

## ADR-007 — Lenguaje: Kotlin · Build: Gradle (KTS) · minSdk 26 · targetSdk 35

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

minSdk 26 cubre Android 8.0+. SpeechRecognizer está disponible desde API 8.

---

## ADR-008 — Sin persistencia de audio ni texto

**Fecha:** 2026-10-29  
**Estado:** Aceptada  

El teclado no graba, no registra ni cachea audio ni texto transcrito.

---

## [EJECUTOR] — aapt2 tomado del SDK local (build-tools 34.0.0)
**Fecha:** 2026-10-29  

El entorno de red intercepta SSL y Java no puede descargar el artifact
`com.android.tools.build:aapt2` de Maven. Se descargó el jar oficial a
`tools/aapt2/aapt2.exe` (vía PowerShell) y `gradle.properties` fija
`android.aapt2FromMavenOverride` a esa ruta. Nota de máquina: el wrapper
usa Gradle 8.9; build-tools instalados 34.0.0/36.1.0/37.0.0 (35.0.0 ya no
está disponible en este sdkmanager).

---

## [EJECUTOR] — `android:exported="true"` en el servicio IME

**Fecha:** 2026-10-29  

Con `targetSdk` 35 el manifiesto exige `android:exported` explícito en
componentes con `intent-filter`. El snippet de T002 no lo traía; se añadió
`android:exported="true"` al `<service>` de `TecladoIME` (un IME es por
definición invocado desde el sistema).

---

## [EJECUTOR] — Notas de implementación T004-T006

**Fecha:** 2026-10-29  

- Fuentes en `app/src/main/java/com/yolatam/teclado/` (los tasks citaban
  `src/main/kotlin/...`; AGP incluye ambos, se mantiene la convención del
  proyecto iniciada en T002/T003).
- Dependencias OkHttp 4.12.0 y kotlinx-coroutines-android 1.7.3 aprobadas
  explícitamente en T005; versiones exactas del task.
- `InputMethodService` no es `LifecycleOwner`: en vez de `lifecycleScope`
  se usa un `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`
  cancelado en `onDestroy()`.
- Cambios en `local.properties` no invalidan `generateDebugBuildConfig`
  (Gradle no lo trackea como input): si la API key no aparece en
  `BuildConfig.java`, tocar `app/build.gradle.kts` o hacer clean.

---

## [EJECUTOR] - <queries> para RecognitionService (fix T006)

**Fecha:** 2026-10-29

En Android 11+ el filtrado de visibilidad de paquetes impide que
SpeechRecognizer resuelva el servicio de voz del sistema sin declaracion
<queries> en el manifest: startListening quedaba muerto en silencio (el panel
se quedaba en rojo). Anadido el <queries> con la accion
android.speech.RecognitionService y chequeo isRecognitionAvailable() con
log+onError en GoogleTranscriptionEngine.

---

## [EJECUTOR] - Micro en modo push-to-talk (T006)

**Fecha:** 2026-10-29

El texto del panel dice 'Suelta para enviar' y el onClick del task
contradice ese gesto (el click se dispara al soltar: el boton quedaba
verde mientras se hablaba). Cambiado a OnTouchListener: ACTION_DOWN
inicia escucha, ACTION_UP/CANCEL la detiene. Ademas startListening
destruye el recognizer anterior (evita fuga de instancias).

---

## [EJECUTOR] - Notas T007 (settings + release limpia)

**Fecha:** 2026-10-29

- SettingsActivity usa android.app.Activity (AppCompatActivity exigiria
anadir androidx.appcompat sin aprobar).
- Eliminado buildConfigField OPENAI_API_KEY: el APK publico no debe
transportar ninguna key (ApiKeyManager/EncryptedSharedPreferences).
- security-crypto y sus deps de Google Maven se sirven desde un repo
maven local 'vendor-repo/' (la red intercepta SSL de dl.google.com y
Java no puede descargar). Anadido a settings.gradle.kts.
- ?attr/selectableItemBackgroundBorderless -> ?android:attr/... (attr de
framework, no hay tema AppCompat).
- 'Claude outputs/' a .gitignore: material interno del planificador.
