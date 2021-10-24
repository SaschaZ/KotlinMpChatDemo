import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    application
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "dev.zieger.mpchatdemo.server"
version = "1.0-SNAPSHOT"

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project
val exposedVersion: String by project
val kotlinSerializationVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.221-kotlin-1.5.21")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.xerial:sqlite-jdbc:3.30.1")

                implementation("org.slf4j:slf4j-log4j12:1.7.32")
            }
        }
        val jvmTest by getting
    }
}

application {
    mainClass.set("dev.zieger.mpchatdemo.server.ServerMainKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = rootProject.allprojects
        .first {it.name == "web" }
        .getAllTasks(true)
        .flatMap { it.value.toList() }
        .firstOrNull { it.name == "jsBrowserDistribution" }
    dependsOn(jsBrowserDistribution)
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Copy>("jvmProcessResources"))
    dependsOn(tasks.named<Jar>("jvmJar"))

    classpath(tasks.named<Jar>("jvmJar"))
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("MpChatServerDemo.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "dev.zieger.mpchatdemo.server.ServerMainKt"))
        }
        destinationDirectory.set(File(rootProject.projectDir.path))
    }
}