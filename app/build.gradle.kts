import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Load local.properties for signing config
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    FileInputStream(localPropertiesFile).use { localProperties.load(it) }
}

android {
    namespace = "com.nityapooja.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nityapooja.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        // Spotify integration
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"9bfdad45bbb94568bbcaf38dc48c2f8a\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"nityapooja://spotify-callback\"")
        manifestPlaceholders["redirectSchemeName"] = "nityapooja"
        manifestPlaceholders["redirectHostName"] = "spotify-callback"

        // AdMob Banner Ad Unit ID
        buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-4962910048695842/3520646134\"")
    }

    signingConfigs {
        create("release") {
            // To configure release signing, add these to your local.properties file:
            //   RELEASE_STORE_FILE=../keystore/release.jks
            //   RELEASE_STORE_PASSWORD=your_password
            //   RELEASE_KEY_ALIAS=your_alias
            //   RELEASE_KEY_PASSWORD=your_key_password
            val storeFilePath = localProperties.getProperty("RELEASE_STORE_FILE")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
            }
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use release signing config when keystore is configured
            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig?.storeFile?.exists() == true) {
                signingConfig = releaseSigningConfig
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.animation)
    implementation(libs.compose.navigation)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // Coroutines
    implementation(libs.coroutines.android)

    // Coil
    implementation(libs.coil.compose)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // WorkManager
    implementation(libs.work.runtime)

    // DataStore
    implementation(libs.datastore.preferences)

    // Splash
    implementation(libs.splashscreen)

    // Lottie
    implementation(libs.lottie.compose)

    // Billing (add back when ready for subscriptions)
    // implementation(libs.billing)

    // Ads — Google AdMob
    implementation(libs.play.ads)

    // Spotify SDK (place AARs in app/libs/)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
}
