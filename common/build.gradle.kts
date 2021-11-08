plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "15"
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

                val kotlinCoroutinesVersion: String by project
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

                val ktorVersion: String by project
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")

                val kotlinSerializationVersion: String by project
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.common)

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
    }
}
