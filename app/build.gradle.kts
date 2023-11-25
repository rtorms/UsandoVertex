import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "br.utfpr.usandovertex"
    compileSdk = 34


    defaultConfig {
        applicationId = "br.utfpr.usandovertex"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }

    packagingOptions {
        exclude( "META-INF/INDEX.LIST")
        exclude( "META-INF/DEPENDENCIES")
    }

}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.cloud:google-cloud-vision:3.28.0")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.api-client:google-api-client:1.32.1")
    runtimeOnly("io.grpc:grpc-android:1.59.0")
    implementation( "io.grpc:grpc-okhttp:1.59.0")
















}




