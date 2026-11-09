# T006-fix4 · Estado "Preparando..." entre grabación y sugerencias

Estado: `[ ]` pendiente
Fase: 1 — Motor Google SpeechRecognizer
Depende de: T006-fix3 ✅

## Problema
Entre que el usuario suelta el micrófono y aparecen las sugerencias hay 2-3 segundos
en blanco mientras el LLM procesa. El usuario no sabe si algo está pasando.

## Objetivo
Añadir un 4º estado visual `PREPARANDO` con spinner + texto "Preparando..."
que aparece inmediatamente tras recibir la transcripción y desaparece cuando
llegan las sugerencias del LLM.

## Flujo corregido
```
[REPOSO] → pulsar mic → [GRABANDO] → soltar → recibir transcripción
         → [PREPARANDO] (spinner, 2-3s) → respuesta LLM → [SUGERENCIAS]
```

---

## 1. Modificar `TecladoIME.kt`

### 1a. Ampliar el enum PanelEstado
```kotlin
enum class PanelEstado { REPOSO, GRABANDO, PREPARANDO, SUGERENCIAS }
```

### 1b. Añadir referencia al nuevo layout en las propiedades de la clase
```kotlin
private lateinit var estadoPreparando: View
```

### 1c. En `onCreateInputView()`, justo después de las otras asignaciones de `estadoXxx`:
```kotlin
estadoPreparando = view.findViewById(R.id.estadoPreparando)
```

### 1d. En `mostrarEstado()`, añadir la línea del nuevo estado:
```kotlin
private fun mostrarEstado(estado: PanelEstado) {
    estadoReposo.visibility    = if (estado == PanelEstado.REPOSO)      View.VISIBLE else View.GONE
    estadoGrabando.visibility  = if (estado == PanelEstado.GRABANDO)    View.VISIBLE else View.GONE
    estadoPreparando.visibility= if (estado == PanelEstado.PREPARANDO)  View.VISIBLE else View.GONE
    estadoSugerencias.visibility = if (estado == PanelEstado.SUGERENCIAS) View.VISIBLE else View.GONE
    estadoActual = estado
}
```

### 1e. En el `onResult` del listener de transcripción, mostrar PREPARANDO antes de llamar al LLM:

Buscar este bloque:
```kotlin
onResult = { crudo ->
    Log.d("TecladoIME", "Resultado: $crudo")
    textoCrudo = crudo
    servicioScope.launch {
        try {
            val sug = suggestionEngine.generar(textoCrudo)
```

Reemplazarlo por:
```kotlin
onResult = { crudo ->
    Log.d("TecladoIME", "Resultado: $crudo")
    textoCrudo = crudo
    mostrarEstado(PanelEstado.PREPARANDO)   // ← NUEVA LÍNEA
    servicioScope.launch {
        try {
            val sug = suggestionEngine.generar(textoCrudo)
```

**Nota:** `onResult` se llama desde el hilo principal (el RecognitionListener corre en main
thread), así que `mostrarEstado()` se puede llamar directamente sin `post {}`.

---

## 2. Modificar `keyboard_view.xml`

Añadir el nuevo bloque **entre** el cierre de `estadoGrabando` (`</LinearLayout>`)
y la apertura de `estadoSugerencias` (`<LinearLayout android:id="@+id/estadoSugerencias"`):

```xml
<!-- ESTADO 3: Preparando (spinner mientras el LLM procesa) -->
<LinearLayout
    android:id="@+id/estadoPreparando"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="220dp"
    android:orientation="vertical"
    android:gravity="center"
    android:paddingTop="24dp"
    android:paddingBottom="24dp"
    android:visibility="gone">

    <ProgressBar
        android:id="@+id/spinnerPreparando"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:indeterminate="true"
        android:indeterminateTint="#4CAF50"
        android:layout_marginBottom="16dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Preparando..."
        android:textColor="#8E8E93"
        android:textSize="14sp" />

</LinearLayout>
```

El bloque de `estadoSugerencias` pasa a ser el **ESTADO 4** (cambiar solo el comentario,
el `android:id` no cambia):
```xml
<!-- ESTADO 4: Sugerencias -->
```

---

## Verificación
1. Build + install APK
2. Abrir WhatsApp → activar Teclado Yolatam
3. Pulsar mic → hablar → soltar
4. Debe aparecer spinner verde + "Preparando..." durante ~2-3s ✓
5. Luego aparece el lienzo con chips de sugerencias ✓
6. Si el LLM falla → vuelve a REPOSO (el catch ya lo hace) ✓

## Commit
`feat(fase-1): añadir estado PREPARANDO con spinner entre grabación y LLM (T006-fix4)`
