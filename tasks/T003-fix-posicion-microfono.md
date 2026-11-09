# T003-fix · Corregir posición del botón de micrófono

Estado: `[x]` completada
Depende de: T003 ✅ (build OK, log OK, pero botón muy abajo)

## Problema
El botón de micrófono aparece demasiado abajo en la pantalla.
El layout actual tiene `height="220dp"` con `layout_gravity="center"`,
pero Android 15 añade insets de navegación que desplazan el contenido hacia abajo.

## Archivo a modificar
`app/src/main/res/layout/keyboard_view.xml`

## Cambio concreto
Sustituir el contenido COMPLETO del archivo por esto:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center"
    android:background="#1C1C1E"
    android:minHeight="220dp"
    android:paddingTop="24dp"
    android:paddingBottom="24dp">

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
```

Cambios clave:
- `FrameLayout` → `LinearLayout` con `gravity="center"` (centra verticalmente el botón)
- `height="220dp"` → `wrap_content` con `minHeight="220dp"` (el sistema gestiona la altura real)
- `paddingTop/Bottom 24dp` para que el botón respire y no quede pegado a ningún borde

## También modificar TecladoIME.kt
En `onCreateInputView()`, justo después de inflar la vista y antes del return, añadir:

```kotlin
// Adaptar padding al inset de barra de navegación (Android 11+)
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
    view.setOnApplyWindowInsetsListener { v, insets ->
        val navBar = insets.getInsets(android.view.WindowInsets.Type.navigationBars())
        v.setPadding(0, 0, 0, navBar.bottom)
        insets
    }
}
```

## Build y verificar
```
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
Abrir WhatsApp → campo de texto → activar Teclado Yolatam.
El botón debe verse centrado verticalmente en la superficie del teclado.

## Criterio de aceptación
- Botón verde centrado visualmente (no pegado al borde inferior ni superior)
- Sigue funcionando: `adb logcat -s TecladoIME:D` muestra "Micrófono pulsado" al pulsar

## Commit
`fix(fase-1): centrar botón micrófono con LinearLayout y paddingBottom (T003-fix)`


Nota ejecutor: build OK e instalado en Pixel 9a (5C031JEBF05822). Pendiente confirmacion visual del centrado por el usuario.
