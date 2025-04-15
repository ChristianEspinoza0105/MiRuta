plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    }

android {
    namespace = "com.example.miruta"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.miruta"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    //Maps
    implementation ("com.google.android.gms:play-services-maps:19.2.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.37.2")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
    implementation ("com.google.android.gms:play-services-maps:19.2.0")
    implementation ("com.google.maps.android:android-maps-utils:3.11.2")
    implementation ("com.google.maps.android:maps-compose:6.5.2")

    // Firebase BoM: Administra las versiones de las bibliotecas de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Bibliotecas de Firebase
    implementation("com.google.firebase:firebase-auth-ktx") // Autenticación de Firebase
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore de Firebase

    // Dagger Hilt para inyección de dependencias
    implementation("com.google.dagger:hilt-android:2.56.1")
    kapt("com.google.dagger:hilt-compiler:2.56.1")

    // Kotlin estándar
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")

    // Jetpack Compose y componentes relacionados
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")

    // Otras bibliotecas de Jetpack
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Dependencias para pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

