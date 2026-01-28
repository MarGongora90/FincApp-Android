plugins {
    id("com.android.application") version "8.1.0" apply true
}

android {
    namespace = "com.margongora.fincapp"
    compileSdk = 34

    @Suppress("UnstableApiUsage")
    project.extra.set("android.suppressUnsupportedCompileSdk", "34")

    // Ajuste de rutas para asegurar que encuentre el XML y los IDs
    sourceSets {
        getByName("main") {
            manifest.srcFile("app/src/main/AndroidManifest.xml")
            java.srcDirs("app/src/main/java")
            res.srcDirs("app/src/main/res")
            assets.srcDirs("app/src/main/assets")
        }
    }

    defaultConfig {
        applicationId = "com.margongora.fincapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Hemos desactivado ViewBinding para evitar conflictos de nombres con los IDs manuales
    buildFeatures {
        viewBinding = false
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Pruebas unitarias y de interfaz (Requisito Bloque 3)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}