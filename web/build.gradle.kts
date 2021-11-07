plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation(compose.web.core)
                implementation(compose.runtime)

                val ktorVersion: String by project
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")

                val kotlinSerializationVersion: String by project
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
    }
}