import java.util.Properties
import java.io.FileInputStream
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val defaultWebClientId = localProperties.getProperty("defaultWebClientId") ?: ""

android {
    namespace = "com.example.quotion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quotion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"$defaultWebClientId\"")
        manifestPlaceholders["defaultWebClientId"] = defaultWebClientId
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    ndkVersion = "29.0.13113456 rc1"
    buildToolsVersion = "35.0.0"
    buildFeatures {
        buildConfig = true  // Bật tính năng BuildConfig
    }
}

dependencies {

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Declare the dependencies for the desired Firebase products without specifying versions
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Google Sign-In
    // https://mvnrepository.com/artifact/com.google.android.gms/play-services-auth

    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("androidx.credentials:credentials:1.2.0-alpha03")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0-alpha03")
    implementation("com.google.android.gms:play-services-auth:21.3.0")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.inappmessaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}