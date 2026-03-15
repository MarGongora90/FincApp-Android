

plugins {
    // Definimos las versiones compatibles con tu Android Studio
    id("com.android.application") version "8.1.3" apply false
    id("com.android.library") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false

}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}