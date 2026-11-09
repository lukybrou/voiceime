package com.yolatam.teclado

/**
 * Tres versiones mejoradas de un texto dictado (ADR-008: nada se persiste,
 * solo se devuelven en memoria para la UI de T006).
 */
data class Sugerencias(
    val original: String,
    val v1: String,  // neutro con puntuación
    val v2: String,  // informal/expresivo
    val v3: String   // compacto
)

/**
 * Contrato de generación de sugerencias. Igual que TranscriptionEngine,
 * permite cambiar de proveedor de LLM sin tocar el IME.
 */
interface SuggestionEngine {
    suspend fun generar(textoCrudo: String): Sugerencias
}
