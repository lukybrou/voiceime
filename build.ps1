# build.ps1 — wrapper para compilar sin configurar JAVA_HOME manualmente
# Uso: .\build.ps1              (assembleDebug)
#      .\build.ps1 assembleRelease

$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$task = if ($args.Count -gt 0) { $args[0] } else { "assembleDebug" }
.\gradlew $task
