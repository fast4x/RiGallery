import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kspAndroid)
    alias(libs.plugins.roomPlugin)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.baselineProfilePlugin)
    alias(libs.plugins.kotlin.compose.compiler)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "it.fast4x.rigallery"
    compileSdk = 35

    val applicationName = "RiGallery"

    defaultConfig {
        applicationId = "it.fast4x.rigallery"
        minSdk = 30
        targetSdk = 35
        versionCode = 2
        versionName = "0.1.1"

        buildConfigField("String", "APPLICATION_NAME", "\"$applicationName\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        base.archivesName.set("$applicationName-${versionName}" + mapsApiApplicationPrefix)
    }

    lint.baseline = file("lint-baseline.xml")

    signingConfigs {
        create("release") {
            storeFile = file("release_key.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders += mapOf(
                "appProvider" to "it.fast4x.rigallery.debug.media_provider",
                "appName" to "$applicationName-Debug"
            )
            buildConfigField("String", "MAPS_TOKEN", getApiKey())
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("Boolean", "ALLOW_ALL_FILES_ACCESS", allowAllFilesAccess)
            buildConfigField(
                "String",
                "CONTENT_AUTHORITY",
                "\"it.fast4x.rigallery.debug.media_provider\""
            )
        }
        getByName("release") {
            manifestPlaceholders += mapOf(
                "appProvider" to "it.fast4x.rigallery.media_provider",
                "appName" to applicationName
            )
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "ALLOW_ALL_FILES_ACCESS", allowAllFilesAccess)
            buildConfigField("String", "MAPS_TOKEN", getApiKey())
            buildConfigField("String", "CONTENT_AUTHORITY", "\"it.fast4x.rigallery.media_provider\"")
        }
//        create("staging") {
//            initWith(getByName("release"))
//            isMinifyEnabled = false
//            isShrinkResources = false
//            applicationIdSuffix = ".staging"
//            versionNameSuffix = "-staging"
//            manifestPlaceholders["appProvider"] = "com.dot.staging.debug.media_provider"
//            buildConfigField(
//                "String",
//                "CONTENT_AUTHORITY",
//                "\"com.dot.staging.debug.media_provider\""
//            )
//        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeCompiler {
        featureFlags = setOf(
            ComposeFeatureFlag.OptimizeNonSkippingGroups
        )
        includeSourceInformation = true
        stabilityConfigurationFiles = listOf (
            rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas/")
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
    }

    splits {

        // Configures multiple APKs based on ABI.
        abi {

            // Enables building multiple APKs per ABI.
            isEnable = true

            // By default all ABIs are included, so use reset() and include to specify that you only
            // want APKs for x86 and x86_64.

            // Resets the list of ABIs for Gradle to create APKs for to none.
            reset()

            // Specifies a list of ABIs for Gradle to create APKs for.
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")

            // Specifies that you don't want to also generate a universal APK that includes all ABIs.
            isUniversalApk = false
        }
    }

//    flavorDimensions += listOf("abi")
//    productFlavors {
//        create("arm64-v8a") {
//            dimension = "abi"
//            versionCode = 4 + (android.defaultConfig.versionCode ?: 0) * 10
//            ndk.abiFilters.add("arm64-v8a")
//        }
//        create("armeabi-v7a") {
//            dimension = "abi"
//            versionCode = 3 + (android.defaultConfig.versionCode ?: 0) * 10
//            ndk.abiFilters.add("armeabi-v7a")
//        }
//        create("x86_64") {
//            dimension = "abi"
//            versionCode = 2 + (android.defaultConfig.versionCode ?: 0) * 10
//            ndk.abiFilters.add("x86_64")
//        }
//        create("x86") {
//            dimension = "abi"
//            versionCode = 1 + (android.defaultConfig.versionCode ?: 0) * 10
//            ndk.abiFilters.add("x86")
//        }
//        create("universal") {
//            dimension = "abi"
//            versionCode = 0 + (android.defaultConfig.versionCode ?: 0) * 10
//            ndk.abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
//        }
//    }
//
//    applicationVariants.all {
//        outputs.forEach { output ->
//            (output as BaseVariantOutputImpl).outputFileName =
//                "RiGallery-${versionName}-$versionCode-${productFlavors[0].name}" + mapsApiApplicationPrefix + ".apk"
//        }
//    }
}

dependencies {
    implementation(libs.androidx.lifecycle.process)
    runtimeOnly(libs.androidx.profileinstaller)
    implementation(project(":libs:cropper"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Core - Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.lifecycle.runtime)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.startup.runtime)

    // Compose - Shimmer
    implementation(libs.compose.shimmer)
    // Compose - Material3
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window.size)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)

    // Compose - Accompanists
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.drawablepainter)

    // Android MDC - Material
    implementation(libs.material)

    // Kotlin - Coroutines
    implementation(libs.kotlinx.coroutines.core)
    runtimeOnly(libs.kotlinx.coroutines.android)

    implementation(libs.kotlinx.serialization.json)

    // Dagger - Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.ktx)

    // Coil
    implementation(libs.jxl.coder.coil)
    implementation(libs.avif.coder.coil)

    // Sketch
    implementation(libs.sketch.singleton)
    implementation(libs.sketch.compose)
    implementation(libs.sketch.view)
    implementation(libs.sketch.animated.gif)
    implementation(libs.sketch.animated.heif)
    implementation(libs.sketch.animated.webp)
    implementation(libs.sketch.extensions.compose)
    implementation(libs.sketch.http.ktor)
    implementation(libs.sketch.svg)
    implementation(libs.sketch.video)

    // Exo Player
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.compose.video)

    // Exif Interface
    implementation(libs.androidx.exifinterface)

    // Metadata
    implementation(libs.metadata.extractor)

    // Datastore Preferences
    implementation(libs.datastore.prefs)

    // Fuzzy Search
    //implementation(libs.fuzzywuzzy)

    // Aire
    implementation(libs.aire)

    // Subsampling
    implementation(libs.zoomimage.sketch)

    // Splashscreen
    implementation(libs.androidx.core.splashscreen)

    // Jetpack Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.biometric)

    // Composables - Core
    implementation(libs.core)

    // Worker
    implementation(libs.androidx.work.runtime.ktx)

    // Composable - Scrollbar
    implementation(libs.lazycolumnscrollbar)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.gpu)

    implementation(libs.palette)
    implementation(libs.compose.charts)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
}

val mapsApiApplicationPrefix: String
    get() = if (getApiKey() == "\"DEBUG\"") {
        "-nomaps"
    } else {
        ""
    }


fun getApiKey(): String {
    val fl = rootProject.file("api.properties")

    return try {
        val properties = Properties()
        properties.load(FileInputStream(fl))
        properties.getProperty("MAPS_TOKEN")
    } catch (e: Exception) {
        "\"DEBUG\""
    }
}

val allowAllFilesAccess: String
    get() {
        val fl = rootProject.file("app.properties")

        return try {
            val properties = Properties()
            properties.load(FileInputStream(fl))
            properties.getProperty("ALL_FILES_ACCESS")
        } catch (e: Exception) {
            "true"
        }
    }

val gitHeadVersion: String
    get() {
        return providers.exec {
            commandLine("git", "show", "-s", "--format=%h", "HEAD")
        }.standardOutput.asText.get().trim()
    }