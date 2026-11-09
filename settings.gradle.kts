pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri(File(settingsDir, "vendor-repo")) }
        google()
        mavenCentral()
    }
}

rootProject.name = "teclado-android"

include(":app")
