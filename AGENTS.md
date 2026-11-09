# Agents · Agentes

This file defines rules and permissions for AI agents (Claude, OpenCode, etc.) working on this repository.

## Roles

| Agente / Agent | Rol / Role |
|---|---|
| Claude (Cowork / claude.ai) | Planner — diseña tareas, especificaciones y prompts · designs tasks, specs and prompts |
| OpenCode | Executor — implementa código, compila y hace push · implements code, builds and pushes |

## Rules for all agents

- **Never hardcode API keys or tokens** in any file. Keys go in `local.properties` (gitignored) or in the user's encrypted on-device storage via `ApiKeyManager`.
- **Never commit `local.properties`** or any file containing secrets.
- **Always build** (`./gradlew assembleDebug`) before committing code changes.
- **Task files** (`tasks/T*.md`) describe what to implement. Read the full spec before starting.
- **One task per commit** — keep the history clean and readable.
- **Commit message format**: `type(scope): description` — e.g. `feat(T007): encrypted API key settings`

## OpenCode — executor rules

- Read `tasks/T*.md` for the full implementation spec before writing any code.
- Do not invent features not described in the task file.
- After a successful build, commit with the message from the task file and push.
- If the build fails, fix the error before committing. Do not commit broken code.
- Do not modify `AGENTS.md`, `opencode.json` or `tasks/` files unless explicitly instructed.

## Planner (Claude) — planner rules

- Write task files in `tasks/T*.md` with complete, unambiguous specs including exact code snippets.
- Mark tasks as `[x]` completed only after the executor confirms BUILD SUCCESSFUL and device validation.
- Keep the task list in `tasks/README.md` up to date.

## Workflow

```
Planner (Claude) → task file (tasks/T*.md)
                 → prompt for OpenCode
Executor (OpenCode) → reads task file
                    → implements code
                    → assembleDebug
                    → commit + push
Planner → validates on device → marks task done → next task
```
