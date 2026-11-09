# T006 · UI del panel — 3 estados visuales

Estado: `[~]` en progreso
Fase: 1 — Motor Google SpeechRecognizer
Depende de: T004 ✅, T005 ✅

## Contexto
Ver ADR-008. El teclado muta entre 3 estados. Esta tarea implementa el layout completo
y la lógica de transición de estados en TecladoIME.kt.

## Objetivo
La superficie del IME pasa de ser solo un botón a un panel completo con lienzo de texto
y chips de versiones. Sin bottom sheet, sin overlay — todo dentro del keyboard_view.xml.

## Layout: sustituir keyboard_view.xml completo

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="220dp"
    android:background="#1C1C1E">

    <!-- ESTADO 1: Reposo — solo micrófono -->
    <LinearLayout
        android:id="@+id/estadoReposo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:minHeight="220dp"
        android:orientation="vertical"
        android:gravity="center"
        android:paddingTop="24dp"
        android:paddingBottom="24dp"
        android:visibility="visible">

        <Button
            android:id="@+id/btnMicrofono"
            android:layout_width="72dp"
            android:layout_height="72dp"
            android:background="@drawable/fondo_boton_microfono"
            android:contentDescription="@string/boton_microfono"
            android:foreground="@drawable/ic_microfono"
            android:foregroundGravity="center"
            android:stateListAnimator="@null"
            android:text="" />

    </LinearLayout>

    <!-- ESTADO 2: Grabando -->
    <LinearLayout
        android:id="@+id/estadoGrabando"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:minHeight="220dp"
        android:orientation="vertical"
        android:gravity="center"
        android:paddingTop="16dp"
        android:paddingBottom="24dp"
        android:visibility="gone">

        <TextView
            android:id="@+id/txtCronometro"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="● 0:00"
            android:textColor="#FF5252"
            android:textSize="14sp"
            android:layout_marginBottom="16dp" />

        <Button
            android:id="@+id/btnMicrofonoGrabando"
            android:layout_width="72dp"
            android:layout_height="72dp"
            android:background="@drawable/fondo_boton_microfono_activo"
            android:contentDescription="Grabando"
            android:foreground="@drawable/ic_microfono"
            android:foregroundGravity="center"
            android:stateListAnimator="@null"
            android:text="" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Suelta para enviar"
            android:textColor="#8E8E93"
            android:textSize="12sp"
            android:layout_marginTop="12dp" />

    </LinearLayout>

    <!-- ESTADO 3: Sugerencias -->
    <LinearLayout
        android:id="@+id/estadoSugerencias"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp"
        android:visibility="gone">

        <!-- Lienzo de texto -->
        <TextView
            android:id="@+id/txtTranscripcion"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:minHeight="80dp"
            android:background="#2C2C2E"
            android:padding="12dp"
            android:textColor="#FFFFFF"
            android:textSize="15sp"
            android:lineSpacingExtra="4dp"
            android:text=""
            android:hint="Texto transcrito..."
            android:textColorHint="#636366" />

        <!-- Chips de versiones -->
        <HorizontalScrollView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:scrollbars="none">

            <LinearLayout
                android:id="@+id/contenedorChips"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical">

                <!-- Los chips se añaden dinámicamente en código -->

            </LinearLayout>
        </HorizontalScrollView>

        <!-- Botones de acción -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="10dp"
            android:gravity="center_vertical">

            <Button
                android:id="@+id/btnInsertar"
                android:layout_width="0dp"
                android:layout_height="44dp"
                android:layout_weight="1"
                android:text="Insertar"
                android:textColor="#FFFFFF"
                android:backgroundTint="#4CAF50"
                android:layout_marginEnd="8dp" />

            <Button
                android:id="@+id/btnNuevaGrabacion"
                android:layout_width="44dp"
                android:layout_height="44dp"
                android:text=""
                android:foreground="@drawable/ic_microfono"
                android:foregroundGravity="center"
                android:backgroundTint="#3A3A3C"
                android:contentDescription="Nueva grabación"
                android:stateListAnimator="@null" />

        </LinearLayout>

    </LinearLayout>

</FrameLayout>
```

## Lógica en TecladoIME.kt

Añadir un enum de estado y función de transición:

```kotlin
enum class PanelEstado { REPOSO, GRABANDO, SUGERENCIAS }

private var estadoActual = PanelEstado.REPOSO
private var textoSeleccionado = ""

private fun mostrarEstado(estado: PanelEstado) {
    estadoReposo.visibility = if (estado == PanelEstado.REPOSO) View.VISIBLE else View.GONE
    estadoGrabando.visibility = if (estado == PanelEstado.GRABANDO) View.VISIBLE else View.GONE
    estadoSugerencias.visibility = if (estado == PanelEstado.SUGERENCIAS) View.VISIBLE else View.GONE
    estadoActual = estado
}
```

Flujo completo de listeners:

```kotlin
// Mic reposo → iniciar grabación
btnMicrofono.setOnClickListener {
    mostrarEstado(PanelEstado.GRABANDO)
    transcriptionEngine.startListening(
        onResult = { crudo ->
            lifecycleScope.launch {
                val sug = suggestionEngine.generar(crudo)
                mostrarSugerencias(sug)
            }
        },
        onError = { mostrarEstado(PanelEstado.REPOSO) }
    )
}

// Mic grabando → detener
btnMicrofonoGrabando.setOnClickListener {
    transcriptionEngine.stopListening()
}

// Insertar texto seleccionado
btnInsertar.setOnClickListener {
    if (textoSeleccionado.isNotEmpty()) {
        currentInputConnection?.commitText(textoSeleccionado, 1)
        mostrarEstado(PanelEstado.REPOSO)
    }
}

// Nueva grabación
btnNuevaGrabacion.setOnClickListener {
    mostrarEstado(PanelEstado.REPOSO)
}
```

Función para mostrar sugerencias y crear chips dinámicamente:

```kotlin
private fun mostrarSugerencias(sug: Sugerencias) {
    txtTranscripcion.text = sug.v1  // mostrar v1 por defecto
    textoSeleccionado = sug.v1

    contenedorChips.removeAllViews()
    listOf("v1 neutro" to sug.v1, "v2 casual" to sug.v2, "v3 corto" to sug.v3).forEach { (label, texto) ->
        val chip = Button(this).apply {
            this.text = label
            setOnClickListener {
                txtTranscripcion.text = texto
                textoSeleccionado = texto
            }
            // Estilo chip oscuro
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF3A3A3C.toInt())
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
```

## Crear drawable: `fondo_boton_microfono_activo.xml`
Igual que `fondo_boton_microfono.xml` pero color rojo (#FF5252) para estado grabando.

## Verificación
1. Build + install
2. Abrir WhatsApp → campo de texto → activar Teclado Yolatam
3. Estado 1: botón verde centrado ✓
4. Pulsar → Estado 2: indicador rojo "Grabando" ✓
5. Hablar → soltar → Estado 3: lienzo con texto + chips v1/v2/v3 ✓
6. Tap chip → texto cambia en lienzo ✓
7. "Insertar" → texto aparece en WhatsApp ✓
8. "🎤" → vuelve a Estado 1 ✓

## Commit
`feat(fase-1): UI panel 3 estados con lienzo y chips de sugerencias (T006)`
