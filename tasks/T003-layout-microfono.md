# T003 · Layout con botón de micrófono

**Estado:** `[~]` en progreso  
**Fase:** 1 — Motor Google SpeechRecognizer  
**Depende de:** T002 ✅

## Objetivo

Añadir una UI mínima al IME: una superficie visible con un botón de micrófono centrado. El teclado debe mostrar algo en pantalla cuando está activo.

## Criterios de aceptación

- [ ] El IME muestra una vista con fondo oscuro (color `#1C1C1E` o similar)
- [ ] Un botón circular con icono de micrófono centrado verticalmente y horizontalmente
- [ ] El botón tiene estado visual: color normal (`#4CAF50` verde) y estado presionado (`#388E3C`)
- [ ] Altura del teclado: mínimo 220dp
- [ ] Al pulsar el botón, por ahora solo hace `Log.d("TecladoIME", "Micrófono pulsado")` (la lógica de audio va en T004)
- [ ] El icono de micrófono usa `android.R.drawable.ic_btn_speak_now` o un vector drawable propio en `res/drawable/`
- [ ] No crashea al rotar el dispositivo

## Archivos a crear/modificar

- `app/src/main/res/layout/keyboard_view.xml` — layout principal del IME
- `app/src/main/kotlin/es/mod/teclado/TecladoIME.kt` — sobreescribir `onCreateInputView()` para inflar el layout y conectar el botón
- (opcional) `app/src/main/res/drawable/ic_microfono.xml` — vector drawable del micrófono si no se usa el del sistema

## Notas técnicas

- `onCreateInputView()` debe devolver la View inflada desde `keyboard_view.xml`
- Usar `LayoutInflater.from(this).inflate(R.layout.keyboard_view, null)`
- El botón se referencia con `findViewById<Button>(R.id.btnMicrofono)`
- No implementar lógica de audio aquí — eso es T004

## Resultado esperado

Al cambiar al Teclado Yolatam en cualquier app, se ve una barra oscura con un botón de micrófono verde. Al pulsarlo aparece el log en Logcat. El teclado no crashea.

## Notas del ejecutor (2026-10-29)

- `assembleDebug`: BUILD SUCCESSFUL (33 tasks).
- La tarea indicaba `app/src/main/kotlin/es/mod/teclado/TecladoIME.kt`; se usó
  la ruta real del proyecto: `app/src/main/java/com/yolatam/teclado/TecladoIME.kt`
  (paquete `com.yolatam.teclado`, conforme a ADR-007 y T002).
- Pendiente de dispositivo: comprobar barra visible, log al pulsar el micrófono y rotación sin crash.

## Commit

`feat(fase-1): añadir layout con botón de micrófono (T003)`
