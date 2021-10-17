plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "dev.zieger.mpchatdemo.web"
version = "1.0-SNAPSHOT"

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project
val exposedVersion: String by project
val kotlinSerializationVersion: String by project

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

                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.221-kotlin-1.5.21")

                implementation("org.slf4j:slf4j-log4j12:1.7.32")
            }
        }
        val jsTest by getting
    }
}