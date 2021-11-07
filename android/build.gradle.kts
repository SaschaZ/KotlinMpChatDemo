plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("kotlin-android")
}

group = "dev.zieger.mpchatdemo.common"
version = "1.0-SNAPSHOT"

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project
val exposedVersion: String by project
val kotlinSerializationVersion: String by project

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-alpha06"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":common"))

    val androidComposeVersion: String by project
    implementation("androidx.compose.runtime:runtime:$androidComposeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$androidComposeVersion")

    implementation(compose.web.widgets)
    implementation("androidx.compose.ui:ui:1.0.5")
    implementation("androidx.compose.foundation:foundation:1.0.5")
    implementation("androidx.compose.material:material:1.0.5")
    implementation("androidx.compose.material:material-icons-core:1.0.5")
    implementation("androidx.compose.material:material-icons-extended:1.0.5")
    implementation("androidx.activity:activity-compose:1.4.0")

    val androidxAppCompatVersion: String by project
    implementation("androidx.appcompat:appcompat:$androidxAppCompatVersion")

    implementation("org.slf4j:slf4j-log4j12:1.7.32")

    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
}