buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("com.google.gms:google-services:${Versions.GOOGLE_SERVICE}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.FIREBASE_CRASHLYTICS}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${LibsVersion.HILT}")
        classpath("org.jmailen.gradle:kotlinter-gradle:3.6.0")
    }
}

plugins {
    id("org.jmailen.kotlinter") version "3.6.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}