import kotlin.reflect.KMutableProperty0

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
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

    val kotlinCoroutinesVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    val kotlinSerializationVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

    val androidxAppCompatVersion: String by project
    implementation("androidx.appcompat:appcompat:$androidxAppCompatVersion")

    val ktorVersion: String by project
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation(compose.runtime)
    implementation(compose.web.widgets)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.materialIconsExtended)

    val androidXActivityCompose: String by project
    implementation("androidx.activity:activity-compose:$androidXActivityCompose")

    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
}


