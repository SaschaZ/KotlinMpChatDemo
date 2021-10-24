

rootProject.name = "KotlinMpChatDemo"
include(":android:", ":common", ":desktop", ":server", ":server_socket", ":web")

pluginManagement {
    val kotlinVersion: String by settings
    val composeVersion: String by settings
    val androidGradlePluginVersion: String by settings
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("org.jetbrains.compose") version composeVersion
        id("com.android.application") version androidGradlePluginVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        google()
        maven("https://jitpack.io")

        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://plugins.gradle.org/m2/")
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> {
                    useModule("${requested.id.id}:${requested.id.name}:$androidGradlePluginVersion")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        jcenter() // Warning: this repository is going to shut down soon
        mavenLocal()

        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://plugins.gradle.org/m2/")
    }

//    versionCatalogs {
//        create("libs") {
//            val kotlinVersion: String by settings
//            version("kotlin", kotlinVersion)
//
//            alias("kotlin-reflect").to("org.jetbrains.kotlin", "kotlin-reflect")
//                .versionRef("kotlin")
//            version("coroutines", "1.+")
//            alias("coroutines").to("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
//                .versionRef("coroutines")
//            bundle("kotlin", listOf("kotlin-reflect", "coroutines"))
//
//            alias("constraintLayout").to("androidx.constraintlayout:constraintlayout:2.+")
//            alias("coordinatorLayout").to("androidx.coordinatorlayout:coordinatorlayout:1.+")
//            alias("appcompat").to("androidx.appcompat:appcompat:1.+")
//            alias("recyclerview").to("androidx.recyclerview:recyclerview:1.+")
//            alias("material").to("com.google.android.material:material:1.+")
//
//            alias("core-ktx").to("androidx.core:core-ktx:1.+")
//            alias("fragment-ktx").to("androidx.fragment:fragment-ktx:1.+")
//            bundle("ktx", listOf("core-ktx", "fragment-ktx"))
//
//            version("lifecycle", "2.+")
//            alias("viewmodel-ktx").to("androidx.lifecycle", "lifecycle-viewmodel-ktx").versionRef("lifecycle")
//            alias("livedata-ktx").to("androidx.lifecycle", "lifecycle-livedata-ktx").versionRef("lifecycle")
//            alias("lifecycle-common").to("androidx.lifecycle", "lifecycle-common-java8").versionRef("lifecycle")
//            bundle("lifecycle", listOf("viewmodel-ktx", "livedata-ktx", "lifecycle-common"))
//
//            val utilsVersion: String by settings
//            alias("utilsTime").to("dev.zieger.utils:time:$utilsVersion")
//            alias("utilsLog").to("dev.zieger.utils:log:$utilsVersion")
//            alias("utilsMisc").to("dev.zieger.utils:misc:$utilsVersion")
//            alias("utilsCoroutines").to("dev.zieger.utils:coroutines:$utilsVersion")
//            alias("utilsObservables").to("dev.zieger.utils:observables:$utilsVersion")
//            alias("utilsStateMachine").to("dev.zieger.utils:statemachine:$utilsVersion")
//            bundle("utils", listOf("utilsTime", "utilsLog", "utilsMisc", "utilsCoroutines", "utilsObservables"))
//        }
//    }
}