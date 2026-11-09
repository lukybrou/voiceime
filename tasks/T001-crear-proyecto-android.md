# Tarea: Crear proyecto Android vacío

**Estado:** `[x]` completada  
**Fase:** 1  
**Fecha creada:** 2026-10-29  

## Objetivo

Tener un proyecto Android en `C:\yolatam\teclado-android` que compile sin errores
con `./gradlew assembleDebug`.

## Contexto

El repositorio está vacío. Esta tarea crea la base sobre la que irán el IME,
el motor y la pantalla de ajustes. Ver ADR-007 en `docs/DECISIONS.md`.

## Qué hacer

1. Crear proyecto Android con Android Studio (o Gradle directo) dentro de `C:\yolatam\teclado-android`.
   - Paquete: `com.yolatam.teclado`
   - Lenguaje: Kotlin
   - Build scripts: Gradle KTS (`.gradle.kts`)
   - `minSdk` 26, `targetSdk` 35
   - Sin Activity inicial (o con una vacía que se borrará después).
2. Verificar que el proyecto compila: `./gradlew assembleDebug` sin errores.
3. Crear `.gitignore` estándar de Android si Android Studio no lo generó.

## Fuera de alcance

- No añadir ningún código de IME ni de micrófono.
- No añadir dependencias externas.

## Cómo se comprueba

```
./gradlew assembleDebug
```

Resultado esperado: `BUILD SUCCESSFUL`. El APK aparece en
`app/build/outputs/apk/debug/app-debug.apk`.

Anotar aquí al terminar:
- Versión de Gradle: 8.9
- Versión del plugin de Android: AGP 8.7.0 (Kotlin 2.0.21)

---

## Resultado (2026-10-29)

- **Estado:** `[x]` completada
- Gradle: 8.9 (wrapper) · Plugin Android: AGP 8.7.0 · Kotlin: 2.0.21
- `./gradlew assembleDebug` → `BUILD SUCCESSFUL in 8s`
- APK: `app/build/outputs/apk/debug/app-debug.apk`
- JDK: `C:\Program Files\Android\Android Studio\jbr` (fijado en `gradle.properties`)
- aapt2: `tools/aapt2/aapt2.exe` (AGP 8.7.0, descargado localmente)
