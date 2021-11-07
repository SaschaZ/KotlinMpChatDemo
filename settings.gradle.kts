

rootProject.name = "KotlinMpChatDemo"
include(":android:", ":common", ":desktop", ":server", ":web")

pluginManagement {
    val kotlinVersion: String by settings
    val composeVersion: String by settings
    val androidGradlePluginVersion: String by settings
    val shadowJarVersion: String by settings
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.compose") version composeVersion
        id("com.android.application") version androidGradlePluginVersion
        id("com.github.johnrengelman.shadow") version shadowJarVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> {
                    useModule(
                        "${requested.id.id}:" +
                                "${requested.id.name}:$androidGradlePluginVersion"
                    )
                }
            }
        }
    }
}