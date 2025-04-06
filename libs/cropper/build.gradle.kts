plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.smarttoolfactory.cropper"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        create("staging") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    composeCompiler {
        includeSourceInformation = true
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    implementation(project(":libs:gesture"))
}