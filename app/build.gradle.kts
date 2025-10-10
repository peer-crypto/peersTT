plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6") // viewModelScope
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0") // sicherheitshalber
}


android {
    namespace = "com.example.ptt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ptt"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        getByName("debug") {
            // Deine bestehende Debug-App bleibt: com.example.ptt
            // Optional erkennbar machen:
            versionNameSuffix = "-debug"
            // resValue("string", "app_name", "PTT (Debug)")
        }

        // Zweite Debug-Variante, parallel installierbar
        create("dev") {
            initWith(getByName("debug"))           // erbt Debug-Einstellungen (debuggable etc.)
            applicationIdSuffix = ".dev"           // -> com.example.ptt.dev
            versionNameSuffix = "-dev"
            // Optional eigener Anzeigename:
            // resValue("string", "app_name", "PTT (Dev)")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    implementation("androidx.compose.ui:ui-text")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

}