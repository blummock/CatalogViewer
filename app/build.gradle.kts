import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.catalogviewer"
    compileSdkVersion(libs.versions.compileSdk.get().toInt())

    defaultConfig {
        applicationId = "com.example.catalogviewer"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get().toInt())
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":data"))

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.main)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ksp
    ksp(libs.kotlin.metadata.jvm)
}