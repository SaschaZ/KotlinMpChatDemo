plugins {
    id("com.android.application")
    kotlin("android")
//    kotlin("multiplatform")
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
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(31)
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
    implementation("androidx.compose.runtime:runtime-rxjava2:$androidComposeVersion")

    val androidxCoreKtxVersion: String by project
    implementation("androidx.core:core-ktx:$androidxCoreKtxVersion")
    val androidxAppCompatVersion: String by project
    implementation("androidx.appcompat:appcompat:$androidxAppCompatVersion")
    val googleAndroidMaterialVersion: String by project
    implementation("com.google.android.material:material:$googleAndroidMaterialVersion")
    val androidxConstraintLayoutVersion: String by project
    implementation("androidx.constraintlayout:constraintlayout:$androidxConstraintLayoutVersion")
    val androidxVectorDrawablesVersion: String by project
    implementation("androidx.vectordrawable:vectordrawable:$androidxVectorDrawablesVersion")
    val androidxLifeCycleLiveDataKtxVersion: String by project
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifeCycleLiveDataKtxVersion")
    val androidxLifeCycleViewModelVersion: String by project
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidxLifeCycleViewModelVersion")
    val androidxNavigationFragmentKtxVersion: String by project
    implementation("androidx.navigation:navigation-fragment-ktx:$androidxNavigationFragmentKtxVersion")
    val androidxNavigationUiKtxVersion: String by project
    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationUiKtxVersion")
    val androidxFragmentKtxVersion: String by project
    implementation("androidx.fragment:fragment-ktx:$androidxFragmentKtxVersion")
    val androidxLegacyVersion: String by project
    implementation("androidx.legacy:legacy-support-v4:$androidxLegacyVersion")

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
}