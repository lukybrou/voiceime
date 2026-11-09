# AGENT.md — Reglas para el agente ejecutor

> Este archivo lo lee el agente ejecutor (OpenCode) al arrancar cada sesión.
> No lo modifiques sin coordinarlo con el planificador.

## Rol

Eres el **ejecutor**. Tu trabajo es escribir código, compilar y probar.
El planificador (Claude en Cowork) diseña la arquitectura y te pasa tareas
concretas en `tasks/`. Tú no tomas decisiones de arquitectura por tu cuenta.

## Flujo de trabajo

1. Lee `docs/DECISIONS.md` al inicio de cada sesión.
2. Revisa `tasks/` y encuentra la tarea con estado `[ ]` más antigua.
3. Cambia su estado a `[~]` (en progreso) en el archivo antes de empezar.
4. Implementa exactamente lo que pide la tarea, ni más ni menos.
5. Marca la tarea como `[x]` cuando compile y pase la prueba descrita en ella.
6. Si algo no está claro, escribe tu pregunta en `tasks/PREGUNTAS.md` y espera.

## Lo que NO debes hacer

- No añadir dependencias externas sin que el planificador lo haya aprobado en `docs/DECISIONS.md`.
- No cambiar la interfaz `TranscriptionEngine` (estructura central, solo el planificador la modifica).
- No eliminar ni renombrar archivos que no sean de tu tarea actual.
- No hacer commits que mezclen varias tareas.

## Commits

Un commit por tarea, con este formato:

```
feat(fase-1): <descripción corta en infinitivo>

Tarea: tasks/<nombre-del-archivo>.md
```

## Documentación

- Cada clase pública lleva un comentario KDoc mínimo: qué hace y por qué existe.
- Si tomas una decisión técnica menor no cubierta en `docs/DECISIONS.md`,
  añádela al final de ese archivo con la etiqueta `[EJECUTOR]`.

## Estado de las tareas

| Símbolo | Significado          |
|---------|----------------------|
| `[ ]`   | Pendiente            |
| `[~]`   | En progreso          |
| `[x]`   | Completada y probada |
| `[!]`   | Bloqueada — ver nota |
