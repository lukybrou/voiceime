# Tarea: IME vacío registrado en el sistema

**Estado:** `[~]` en progreso  
**Fase:** 1  
**Fecha creada:** 2026-10-29  

## Objetivo

El teclado aparece en la lista de teclados de Android y puede activarse,
aunque todavía no muestra ninguna tecla.

## Contexto

El proyecto compila limpio (T001 completada). Ver ADR-001 y ADR-007 en
`docs/DECISIONS.md`. Ver `docs/ARCHITECTURE.md` para la estructura de clases.

## Qué hacer

1. Crear `app/src/main/java/com/yolatam/teclado/TecladoIME.kt`
   - Extiende `InputMethodService`
   - Override de `onCreateInputView()` que devuelve una `View` vacía por ahora
   - KDoc mínimo en la clase

2. Crear `app/src/main/res/xml/metodo_entrada.xml`
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <input-method xmlns:android="http://schemas.android.com/apk/res/android" />
   ```

3. Registrar el IME en `AndroidManifest.xml`:
   ```xml
   <service
       android:name=".TecladoIME"
       android:label="Teclado Yolatam"
       android:permission="android.permission.BIND_INPUT_METHOD">
       <intent-filter>
           <action android:name="android.view.InputMethod" />
       </intent-filter>
       <meta-data
           android:name="android.view.im"
           android:resource="@xml/metodo_entrada" />
   </service>
   ```

4. Compilar: `.\gradlew assembleDebug` (sin `$env:JAVA_HOME` — ya está en
   `gradle.properties`)

## Fuera de alcance

- No añadir botón de micrófono todavía
- No añadir `TranscriptionEngine` todavía
- No añadir `SettingsActivity` todavía

## Cómo se comprueba

1. `.\gradlew assembleDebug` → `BUILD SUCCESSFUL`
2. Instalar el APK en el móvil: `adb install -r app\build\outputs\apk\debug\app-debug.apk`
3. En el móvil: Ajustes → Sistema → Idioma e introducción de texto → Teclado en pantalla
4. El teclado "Teclado Yolatam" aparece en la lista y puede activarse

Anotar aquí al terminar:
- Resultado de assembleDebug: BUILD SUCCESSFUL (33 actionable tasks). Nota: hubo que añadir `android:exported="true"` al `<service>` — obligatorio con targetSdk ≥ 31 (ver nota [EJECUTOR] en docs/DECISIONS.md).
- Confirmación de que aparece en la lista del móvil: pendiente — sin dispositivo conectado al adb del PC. Ejecutar: `adb install -r app\build\outputs\apk\debug\app-debug.apk` y revisar Ajustes → Teclados.
