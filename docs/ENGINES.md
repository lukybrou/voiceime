# Motores de transcripción — Candidatos

Datos recogidos el 29-10-2026. Fuentes sin abrir completas; conviene revisarlas.

## Fase 1 (implementado)

| Motor | Uso | Español | Notas |
|---|---|---|---|
| Google `SpeechRecognizer` | Clase nativa de Android | Sí, configurable | Gratis. Sin servidor externo. Motor de la Fase 1 |

## Fase 2 (candidatos, sin implementar)

| Motor | Protocolo | Español | Notas |
|---|---|---|---|
| `qwen-audio-3.1-asr-flash-message` | WebSocket + PCM 16 kHz | Sí (`es` en `language_hints`) | **Candidato principal.** Alibaba lo recomienda para teclados y mensajes de voz. Transcripción incremental, pulido opcional |
| `qwen-audio-3.1-asr-flash-streaming` | WebSocket | Por confirmar | ~160 ms hasta primer texto (nota de prensa) |
| `fun-asr-flash` (2026-06-15) | API Model Studio | Sí | Alternativa para comparar |
| `qwen3-asr-flash` | API síncrona (OpenAI-compatible / DashScope / OpenRouter) | Sí (11 idiomas) | La más simple: graba → envía → recibe texto |
| Whisper (OpenAI) | API externa | Sí | No investigado |
| Gemini | API externa (acepta audio) | Sí | No investigado |

> **Paraformer** (familia antigua de Alibaba): descartada. Su propia documentación pide migrar a Fun-ASR o Qwen-ASR.

## Pendiente antes de elegir motor en Fase 2

- Confirmar qué modelo tiene Douglas en su cuenta de Alibaba Cloud y en qué región (Singapur o Pekín — las claves API son distintas).
- Comparar candidatos con las mismas frases en español grabadas por Douglas, con y sin ruido.

## Fuentes

- [Alibaba: modelos de voz a texto](https://help.aliyun.com/en/model-studio/asr-model)
- [Alibaba: ficha qwen-audio-3.1-asr-flash-message](https://www.alibabacloud.com/help/en/model-studio/qwen-audio-3-1-asr-flash-message)
- [Alibaba: eventos cliente con language_hints](https://www.alibabacloud.com/help/id/model-studio/qwen-asr-message-client-events)
- [Alibaba: referencia API Qwen-ASR](https://www.alibabacloud.com/help/en/model-studio/qwen-asr-api-reference)
- [OpenRouter: Qwen3 ASR Flash](https://openrouter.ai/qwen/qwen3-asr-flash-2026-02-10)
- [36Kr: Alibaba lanza Qwen Audio 3.1](https://eu.36kr.com/en/p/3996196939616387)
