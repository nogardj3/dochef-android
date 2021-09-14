import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id ("com.android.application")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
    id ("kotlin-android")
    id ("org.jetbrains.kotlin.kapt")
}

val admobAppId: String = gradleLocalProperties(rootDir).getProperty("admobAppId")
val admobBannerId: String = gradleLocalProperties(rootDir).getProperty("admobBannerId")

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.yhjoo.dochef"
        minSdk=24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0.0"

        resValue( "string", "admobAppId", admobAppId)
        resValue( "string", "admobBannerId", admobBannerId)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        disable("RtlHardcoded")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.room:room-runtime:2.3.0")
    implementation("androidx.room:room-ktx:2.3.0")
    kapt("androidx.room:room-compiler:2.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.preference:preference-ktx:1.1.1")

    /* Later
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
     */

    // API
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.google.android.gms:play-services-auth:19.2.0")
    implementation ("com.google.android.gms:play-services-ads:20.3.0")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.0")

    // libraries
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation( "io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    kapt ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.orhanobut:logger:2.2.0")
    implementation("com.github.CanHub:Android-Image-Cropper:3.3.5")

    // components
    implementation ("com.github.skydoves:powermenu:2.2.0")
    implementation ("com.afollestad.material-dialogs:core:3.3.0")
    implementation ("com.afollestad.material-dialogs:input:3.3.0")
    implementation ("com.afollestad.material-dialogs:bottomsheets:3.3.0")
    implementation ("com.afollestad.material-dialogs:lifecycle:3.3.0")
    implementation ("com.github.florent37:viewanimator:1.1.2")
    implementation ("com.tbuonomo:dotsindicator:4.2")
    implementation ("com.github.rehmanmuradali:ticker:1.0.1")
    implementation ("com.layer-net:step-indicator:1.1.0")
    implementation ("com.github.mabbas007:TagsEditText:1.0.5")

    // REMOVE SOON
    implementation ("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.25")

    testImplementation ("junit:junit:4.+")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}
