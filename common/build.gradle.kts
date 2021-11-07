plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project
val exposedVersion: String by project
val kotlinSerializationVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "15"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(compose.web.widgets)
                implementation(compose.runtime)

                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

                implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.221-kotlin-1.5.21")

                implementation("org.slf4j:slf4j-log4j12:1.7.32")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.common)

                implementation("org.slf4j:slf4j-log4j12:1.7.32")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation("org.slf4j:slf4j-log4j12:1.7.32")
            }
        }
    }
}