import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

val admobAppId: String = gradleLocalProperties(rootDir).getProperty("admobAppId")
val admobBannerId: String = gradleLocalProperties(rootDir).getProperty("admobBannerId")

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.yhjoo.dochef"
        targetSdk = Versions.TARGET_SDK
        minSdk = Versions.MIN_SDK
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "admobAppId", admobAppId)
        resValue("string", "admobBannerId", admobBannerId)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            manifestPlaceholders["crashlyticsEnabled"] = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        debug {
            manifestPlaceholders["crashlyticsEnabled"] = false
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    lint {
        disable("RtlHardcoded")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // KOTLIN
    implementation("androidx.core:core-ktx:${LibsVersion.CORE_KTX}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${LibsVersion.COROUTINE}")

    // ANDROIDX
    implementation("androidx.appcompat:appcompat:${LibsVersion.APPCOMPAT}")
    implementation("androidx.activity:activity-ktx:${LibsVersion.ACTIVITY_KTX}")
    implementation("androidx.fragment:fragment-ktx:${LibsVersion.FRAGMENT_KTX}")
    implementation("androidx.preference:preference-ktx:${LibsVersion.PREFERENCE}")

    // LIFECYCLE
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${LibsVersion.LIFECYCLE}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${LibsVersion.LIFECYCLE_OTHERS}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${LibsVersion.LIFECYCLE_OTHERS}")
    kapt("androidx.lifecycle:lifecycle-compiler:${LibsVersion.LIFECYCLE_OTHERS}")

    // ROOM
    implementation("androidx.room:room-runtime:${LibsVersion.ROOM}")
    implementation("androidx.room:room-ktx:${LibsVersion.ROOM}")
    kapt("androidx.room:room-compiler:${LibsVersion.ROOM}")

    // NAVIGATION
    implementation("androidx.navigation:navigation-fragment-ktx:${LibsVersion.NAVIGATION}")
    implementation("androidx.navigation:navigation-ui-ktx:${LibsVersion.NAVIGATION}")

    // DATASTORE
    implementation("androidx.datastore:datastore:${LibsVersion.DATASTORE}")
    implementation("androidx.datastore:datastore-preferences:${LibsVersion.DATASTORE}")

    // PAGING
    implementation("androidx.paging:paging-runtime:${LibsVersion.PAGING}")

    // WORKMANAGER
    implementation("androidx.work:work-runtime-ktx:${LibsVersion.WORKMANAGER}")
    implementation("androidx.work:work-multiprocess:${LibsVersion.WORKMANAGER}")

    // HILT
    implementation("com.google.dagger:hilt-android:${LibsVersion.HILT}")
    kapt("com.google.dagger:hilt-android-compiler:${LibsVersion.HILT}")

    // RXJAVA
    implementation("io.reactivex.rxjava3:rxjava:${LibsVersion.REACTIVEX}")
    implementation("io.reactivex.rxjava3:rxandroid:${LibsVersion.REACTIVEX}")

    // RETROFIT
    implementation("com.squareup.retrofit2:retrofit:${LibsVersion.RETROFIT2}")
    implementation("com.squareup.retrofit2:converter-gson:${LibsVersion.RETROFIT2}")

    // GLIDE
    implementation("com.github.bumptech.glide:glide:${LibsVersion.GLIDE}")
    kapt("com.github.bumptech.glide:compiler:${LibsVersion.GLIDE}")

    // UNIT TEST
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:${LibsVersion.MOCKITO}")

    // UI TEST
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:${LibsVersion.ESPRESSO}")

    // GOOGLE
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.android.gms:play-services-ads:20.4.0")
    implementation("com.firebaseui:firebase-ui-storage:8.0.0")

    // others
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.orhanobut:logger:2.2.0")
    implementation("com.github.CanHub:Android-Image-Cropper:3.3.5")
    implementation("com.github.skydoves:powermenu:2.2.0")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:input:3.3.0")
    implementation("com.tbuonomo:dotsindicator:4.2")
    implementation("com.github.mabbas007:TagsEditText:1.0.5")
}
