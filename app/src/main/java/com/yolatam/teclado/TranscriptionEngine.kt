package com.yolatam.teclado

/**
 * Contrato de transcripción de voz a texto (ADR-003).
 *
 * Permite cambiar el motor (Google → Qwen-ASR en Fase 2) sin tocar
 * TecladoIME ni la UI.
 */
interface TranscriptionEngine {
    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit)
    fun stopListening()
    fun destroy()
}
