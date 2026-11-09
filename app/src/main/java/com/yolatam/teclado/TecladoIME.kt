package com.yolatam.teclado

import android.content.res.ColorStateList
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Estados visuales del panel del teclado (T006).
 */
enum class PanelEstado { REPOSO, GRABANDO, PREPARANDO, SUGERENCIAS }

/**
 * IME del teclado Yolatam.
 *
 * Servicio de entrada (ADR-001) con panel de 3 estados: micrófono en reposo,
 * grabación con SpeechRecognizer (ADR-002/003) y sugerencias generadas por
 * LLM en chips seleccionables. El texto elegido se inyecta en la app activa
 * con commitText (ADR-004).
 */
class TecladoIME : InputMethodService() {

    private lateinit var transcriptionEngine: TranscriptionEngine
    private lateinit var suggestionEngine: SuggestionEngine
    private val servicioScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var textoCrudo = ""

    private lateinit var estadoReposo: View
    private lateinit var estadoGrabando: View
    private lateinit var estadoPreparando: View
    private lateinit var estadoSugerencias: View
    private lateinit var txtTranscripcion: TextView
    private lateinit var txtCronometro: TextView
    private lateinit var contenedorChips: LinearLayout

    private var cronometroJob: Job? = null

    private var estadoActual = PanelEstado.REPOSO
    private var textoSeleccionado = ""

    override fun onCreate() {
        super.onCreate()
        transcriptionEngine = GoogleTranscriptionEngine(this)
        suggestionEngine = OpenAISuggestionEngine(BuildConfig.OPENAI_API_KEY)
    }

    override fun onCreateInputView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null)

        estadoReposo = view.findViewById(R.id.estadoReposo)
        estadoGrabando = view.findViewById(R.id.estadoGrabando)
        estadoPreparando = view.findViewById(R.id.estadoPreparando)
        estadoSugerencias = view.findViewById(R.id.estadoSugerencias)
        txtTranscripcion = view.findViewById(R.id.txtTranscripcion)
        txtCronometro = view.findViewById(R.id.txtCronometro)
        contenedorChips = view.findViewById(R.id.contenedorChips)

        view.findViewById<Button>(R.id.btnMicrofono).setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("TecladoIME", "Micrófono pulsado")
                    mostrarEstado(PanelEstado.GRABANDO)
                    estadoGrabando.post {
                        estadoGrabando.requestLayout()
                        estadoGrabando.invalidate()
                    }
                    iniciarCronometro(txtCronometro)
                    transcriptionEngine.startListening(
                        onResult = { crudo ->
                            Log.d("TecladoIME", "Resultado: $crudo")
                            textoCrudo = crudo
                            mostrarEstado(PanelEstado.PREPARANDO)
                            servicioScope.launch {
                                try {
                                    val sug = suggestionEngine.generar(textoCrudo)
                                    Log.d("TecladoIME", "v1: ${sug.v1}")
                                    Log.d("TecladoIME", "v2: ${sug.v2}")
                                    Log.d("TecladoIME", "v3: ${sug.v3}")
                                    mostrarSugerencias(sug)
                                } catch (e: Exception) {
                                    Log.e("TecladoIME", "Error generando sugerencias: ${e.message}")
                                    mostrarEstado(PanelEstado.REPOSO)
                                }
                            }
                        },
                        onError = { msg ->
                            Log.e("TecladoIME", "Error: $msg")
                            mostrarEstado(PanelEstado.REPOSO)
                        }
                    )
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    detenerCronometro()
                    transcriptionEngine.stopListening()
                    v.performClick()
                    true
                }
                else -> false
            }
        }

        view.findViewById<Button>(R.id.btnMicrofonoGrabando).setOnClickListener {
            transcriptionEngine.stopListening()
        }

        view.findViewById<Button>(R.id.btnInsertar).setOnClickListener {
            if (textoSeleccionado.isNotEmpty()) {
                currentInputConnection?.commitText(textoSeleccionado, 1)
                mostrarEstado(PanelEstado.REPOSO)
            }
        }

        view.findViewById<Button>(R.id.btnNuevaGrabacion).setOnClickListener {
            mostrarEstado(PanelEstado.REPOSO)
        }

        mostrarEstado(estadoActual)

        // Adaptar padding al inset de barra de navegación (Android 11+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            view.setOnApplyWindowInsetsListener { v, insets ->
                val navBar = insets.getInsets(android.view.WindowInsets.Type.navigationBars())
                v.setPadding(0, 0, 0, navBar.bottom)
                insets
            }
        }
        return view
    }

    private fun mostrarEstado(estado: PanelEstado) {
        estadoReposo.visibility = if (estado == PanelEstado.REPOSO) View.VISIBLE else View.GONE
        estadoGrabando.visibility = if (estado == PanelEstado.GRABANDO) View.VISIBLE else View.GONE
        estadoPreparando.visibility = if (estado == PanelEstado.PREPARANDO) View.VISIBLE else View.GONE
        estadoSugerencias.visibility = if (estado == PanelEstado.SUGERENCIAS) View.VISIBLE else View.GONE
        if (estado != PanelEstado.GRABANDO) detenerCronometro()
        estadoActual = estado
    }

    private fun iniciarCronometro(txtCronometro: TextView) {
        cronometroJob?.cancel()
        cronometroJob = servicioScope.launch {
            var segundos = 0
            while (true) {
                txtCronometro.text = "● ${segundos / 60}:${(segundos % 60).toString().padStart(2, '0')}"
                delay(1000)
                segundos++
            }
        }
    }

    private fun detenerCronometro() {
        cronometroJob?.cancel()
        cronometroJob = null
    }

    private fun mostrarSugerencias(sug: Sugerencias) {
        txtTranscripcion.text = sug.v1
        textoSeleccionado = sug.v1

        contenedorChips.removeAllViews()
        listOf("v1 neutro" to sug.v1, "v2 casual" to sug.v2, "v3 corto" to sug.v3).forEach { (label, texto) ->
            val chip = Button(this).apply {
                this.text = label
                setOnClickListener {
                    txtTranscripcion.text = texto
                    textoSeleccionado = texto
                }
                backgroundTintList = ColorStateList.valueOf(0xFF3A3A3C.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 8, 0) }
            }
            contenedorChips.addView(chip)
        }

        mostrarEstado(PanelEstado.SUGERENCIAS)
    }

    override fun onDestroy() {
        servicioScope.cancel()
        transcriptionEngine.destroy()
        super.onDestroy()
    }
}
