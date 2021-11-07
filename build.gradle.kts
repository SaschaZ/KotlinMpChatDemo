import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        val kotlinVersion: String by project
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath(kotlin("serialization", version = kotlinVersion))

        val androidGradlePluginVersion: String by project
        classpath("com.android.tools.build:gradle:$androidGradlePluginVersion")
    }
}

allprojects {
    tasks.withType<KotlinCompile>() {
        kotlinOptions.apply {
            // OptIn Annotation
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"

            // Enable experimental APIs
            freeCompilerArgs =
                freeCompilerArgs + "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.Experimental"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=ExperimentalComposeWebWidgetsApi"
        }

        kotlinOptions.jvmTarget = "1.8"
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}