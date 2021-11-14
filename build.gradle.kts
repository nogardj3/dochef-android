buildscript {
    val kotlinMainVersion = "1.5.30"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinMainVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")

//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
    }
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