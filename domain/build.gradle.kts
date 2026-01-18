import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get().toInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get().toInt())
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}