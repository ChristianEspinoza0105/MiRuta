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
    // Retrofit para HTTP
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Kotlin coroutines para Retrofit
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    //ExoPLayer
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")

    //Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okio:okio:3.5.0")

    //TensorFlow
    implementation ("org.tensorflow:tensorflow-lite:2.13.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.2")
    implementation ("org.tensorflow:tensorflow-lite-task-text:0.4.2")

    //OpenCSV
    implementation ("com.opencsv:opencsv:5.5.2")

    //Maps
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation ("com.google.android.gms:play-services-base:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.libraries.places:places:3.3.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-ktx:5.2.0")
    implementation ("com.google.maps.android:android-maps-utils:3.8.0")
    implementation ("com.google.android.gms:play-services-auth:19.0.0")
    implementation ("com.google.maps.android:maps-compose:4.4.1")

    // Firebase BoM: Administra las versiones de las bibliotecas de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    // Bibliotecas de Firebase
    implementation("com.google.firebase:firebase-auth-ktx") // Autenticación de Firebase
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore de Firebase

    // Dagger Hilt para inyección de dependencias
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.litert.metadata)
    implementation(libs.androidx.media3.ui.compose)
    implementation(libs.androidx.compose.testing)
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    // Kotlin estándar
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")

    // Jetpack Compose y componentes relacionados
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation ("androidx.compose.runtime:runtime:1.6.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")
    implementation ("androidx.compose.foundation:foundation:1.6.0")
    implementation ("androidx.compose.animation:animation:1.6.0")
    implementation ("androidx.compose.material3:material3:1.1.1")
    implementation ("androidx.compose.material3:material3-window-size-class:1.1.1")

    // Otras bibliotecas de Jetpack
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Dependencias para pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

