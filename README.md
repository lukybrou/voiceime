# teclado-android

Teclado Android propio con dictado por IA.

El teclado es un IME (InputMethodService) con un botón de micrófono.
Al pulsarlo, transcribe la voz y escribe el texto en la app abierta.

## Documentación

| Archivo | Contenido |
|---|---|
| `AGENT.md` | Reglas para el agente ejecutor (OpenCode) |
| `docs/DECISIONS.md` | Decisiones de arquitectura tomadas |
| `docs/ARCHITECTURE.md` | Diagrama y descripción de componentes |
| `docs/ENGINES.md` | Candidatos de motor de transcripción |
| `tasks/` | Tareas del planificador para el ejecutor |
| `tasks/PLANTILLA.md` | Plantilla para nuevas tareas |
| `tasks/PREGUNTAS.md` | Canal de dudas ejecutor → planificador |

## Fases

- **Fase 1** *(en curso)*: SpeechRecognizer de Android. Sin servidor externo.
- **Fase 2** *(pendiente)*: Motor de IA externa (candidato: Alibaba Qwen-ASR).
- **Fase 3** *(sin decidir)*: Pulido de texto.

## Roles

| Rol | Herramienta | Qué hace |
|---|---|---|
| Planificador | Claude en Cowork | Arquitectura, decisiones, crea tareas en `tasks/` |
| Ejecutor | OpenCode | Implementa las tareas, marca su estado, anota preguntas |
