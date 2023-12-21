//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//        maven(url = "https://plugins.gradle.org/m2/")
//    }
//    dependencies {
//        classpath("com.android.tools.build:gradle:${Config.Version.androidGradlePluginVersion}")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Config.Version.kotlinVersion}")
//        classpath("org.jetbrains.kotlin:kotlin-allopen:${Config.Version.kotlinVersion}")
//    }
//}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}