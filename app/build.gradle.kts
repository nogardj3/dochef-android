import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-android")
    id("kotlin-kapt")
}

val admobAppId: String = gradleLocalProperties(rootDir).getProperty("admobAppId")
val admobBannerId: String = gradleLocalProperties(rootDir).getProperty("admobBannerId")
val tempGoogleIdToken: String = gradleLocalProperties(rootDir).getProperty("tempGoogleIdToken")

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.yhjoo.dochef"
        minSdk = 24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0.0"

        resValue("string", "admobAppId", admobAppId)
        resValue("string", "admobBannerId", admobBannerId)
        resValue("string", "tempGoogleIdToken", tempGoogleIdToken)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    val coreVersion = "1.6.0"
    val coroutineVersion = "1.5.2"
    val appcompatVersion = "1.3.1"
    val activityVersion = "1.3.1"
    val fragmentVersion = "1.3.6"
    val lifecycleVersion = "2.3.1"
    val roomVersion = "2.3.0"
    val navigationVersion = "2.3.5"
    val preferenceVersion = "1.1.1"

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-rc01")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.preference:preference-ktx:$preferenceVersion")

    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

//    val datastoreVersion = "1.0.0"
//    val pagingVersion = "3.0.1"
//    val workManagerVersion = "2.5.0"
//    implementation("androidx.datastore:datastore-preferences:1.0.0")
//    implementation("androidx.paging:paging-runtime:$pagingVersion")
//    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")

    // API
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
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.orhanobut:logger:2.2.0")
    implementation("com.github.CanHub:Android-Image-Cropper:3.3.5")
    implementation("com.github.skydoves:powermenu:2.2.0")
    implementation("com.afollestad.material-dialogs:core:3.3.0")
    implementation("com.afollestad.material-dialogs:input:3.3.0")
    implementation("com.tbuonomo:dotsindicator:4.2")
    implementation("com.github.mabbas007:TagsEditText:1.0.5")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
