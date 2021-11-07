import kotlin.reflect.KMutableProperty0

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("kotlin-android")
}

inline fun <reified T> fromProps(name: String): T = project.properties[name].toString().let {
    when (T::class) {
        Int::class -> it.toInt()
        else -> it
    } as T
}

inline fun <reified V> KMutableProperty0<V>.bindProp() = set(fromProps<V>(name))

android {
    ::compileSdk.bindProp()
    buildToolsVersion = fromProps("androidBuildToolsVersion")

    defaultConfig {
        ::minSdk.bindProp()
        ::targetSdk.bindProp()
        ::versionCode.bindProp()
        ::versionName.bindProp()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        val androidComposeVersion: String by project
        kotlinCompilerExtensionVersion = androidComposeVersion
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

    val ktorVersion: String by project
    val kotlinCoroutinesVersion: String by project
    val kotlinSerializationVersion: String by project
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
}


