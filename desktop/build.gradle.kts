import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project
val kotlinSerializationVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "15"
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation(compose.desktop.currentOs)

                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.zieger.mpchatdemo.desktop.DesktopMainKt"

        nativeDistributions {
            description = "KotlinMpChatDemo"
            packageVersion = "1.0.39"
            targetFormats(Dmg, Msi, Deb)
        }
    }
}