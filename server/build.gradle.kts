import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))

                val ktorVersion: String by project
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")

                val kotlinCssVersion: String by project
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:$kotlinCssVersion")
                val kotlinHtmlVersion: String by project
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinHtmlVersion")

                val kotlinSerializationVersion: String by project
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

                val utilsVersion: String by project
                implementation("dev.zieger.utils:time:${utilsVersion}")

                val exposedVersion: String by project
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                val xerialSqliteVersion: String by project
                implementation("org.xerial:sqlite-jdbc:$xerialSqliteVersion")

                val slf4jVersion: String by project
                implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
            }
        }
    }
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

application {
    mainClass.set("dev.zieger.mpchatdemo.server.ServerMainKt")
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