# ADR-008 · UX del panel de sugerencias

Fecha: 2026-10-29
Estado: Aceptado

## Contexto

El IME necesita una UI para mostrar la transcripción de voz y permitir al usuario elegir entre versiones mejoradas del texto antes de insertarlo en la app destino.

## Decisión

**El teclado es el panel.** La superficie del IME (el `keyboard_view.xml`) muta entre tres estados visuales. No hay bottom sheet ni overlay externo — todo ocurre dentro de la ventana del IME.

El modo por defecto es **modo sugerencias** (no tiempo real). El flujo es:
grabar → procesar → mostrar versiones → el usuario elige → insertar.

El tiempo real queda como modo opcional futuro.

## Estados de la UI

### Estado 1 — Reposo
```
┌─────────────────────────────────┐
│                                 │
│              🎤                 │
│                                 │
└─────────────────────────────────┘
```
- Botón micrófono centrado
- Fondo #1C1C1E

### Estado 2 — Grabando (pulsando mic)
```
┌─────────────────────────────────┐
│  ● 0:03                         │
│                                 │
│           🔴  (animado)         │
│                                 │
└─────────────────────────────────┘
```
- Cronómetro en esquina superior izquierda
- Mic rojo pulsante (animación de onda o escala)
- Suelta para procesar

### Estado 3 — Sugerencias
```
┌─────────────────────────────────┐
│ ┌─────────────────────────────┐ │
│ │ Hola, ¿qué tal estás?       │ │
│ │ ¿Quedamos esta tarde?       │ │
│ └─────────────────────────────┘ │
│                                 │
│  [v1 formal] [v2 casual] [v3 ✨]│
│                                 │
│  [ Insertar ]    [ 🎤 de nuevo ]│
└─────────────────────────────────┘
```
- Lienzo negro con `TextView` (texto aparece de golpe, no letra a letra)
- Chips horizontales con scroll para las versiones (v1, v2, v3)
- Tap en chip → selecciona esa versión → actualiza el TextView
- [ Insertar ] → `currentInputConnection.commitText(texto_seleccionado, 1)`
- [ 🎤 de nuevo ] → vuelve a Estado 1

## Versiones de texto

Generadas por LLM tras recibir la transcripción cruda de SpeechRecognizer:

| Versión | Criterio |
|---------|----------|
| v1 | Puntuación correcta, tono neutro |
| v2 | Más informal/expresivo, con signos de exclamación si aplica |
| v3 | Compacta, sin palabras de relleno |

La transcripción cruda siempre visible en el lienzo para referencia.

## Formato y negritas

- **Puntuación inteligente** siempre (comas, puntos, signos de pregunta/exclamación)
- **Negritas** como chip extra opcional: `[v4 📝]` con markdown `*palabra*` (útil para WhatsApp)
- Decisión de formato depende de app destino — no detectar automáticamente en Fase 1

## Implicaciones en arquitectura de tareas

| Tarea | Responsabilidad |
|-------|-----------------|
| T004 | `TranscriptionEngine` — SpeechRecognizer → texto crudo |
| T005 | `SuggestionEngine` — LLM call → 3 versiones mejoradas |
| T006 | UI del panel de 3 estados (StateFlow / ViewState pattern) |
| T007 | Inserción en campo de texto (`commitText`) + permisos micrófono |

## Consecuencias

- La superficie del IME tiene altura variable: ~160dp en reposo, ~320dp en Estado 3
- El `keyboard_view.xml` pasa a ser un `ViewSwitcher` o un único layout con visibilidad condicional
- El botón mic del T003-fix sigue siendo el punto de entrada — no se descarta, se amplía
- Modo tiempo real queda fuera de scope hasta Fase 2
