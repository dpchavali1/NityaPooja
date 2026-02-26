import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
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
        versionCode = 9
        versionName = "1.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Spotify integration — credentials from local.properties (not committed to VCS)
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"${localProperties.getProperty("SPOTIFY_CLIENT_ID", "")}\"")
        buildConfigField("String", "SPOTIFY_CLIENT_SECRET", "\"${localProperties.getProperty("SPOTIFY_CLIENT_SECRET", "")}\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"nityapooja://spotify-callback\"")
        manifestPlaceholders["redirectSchemeName"] = "nityapooja"
        manifestPlaceholders["redirectHostName"] = "spotify-callback"

        // AdMob Banner Ad Unit ID
        buildConfigField("String", "ADMOB_BANNER_ID", "\"ca-app-pub-4962910048695842/3520646134\"")
    }

    signingConfigs {
        create("release") {
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
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "FULL"
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
    // Shared KMP module
    implementation(project(":shared"))

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

    // Room (needed for RoomDatabase type access)
    implementation(libs.room.runtime)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // Coroutines
    implementation(libs.coroutines.android)

    // Coil 3
    implementation(libs.coil.compose)

    // Network (legacy Retrofit for Spotify in androidApp)
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

    // Encrypted storage
    implementation(libs.security.crypto)

    // Splash
    implementation(libs.splashscreen)

    // Lottie
    implementation(libs.lottie.compose)

    // Ads — Google AdMob
    implementation(libs.play.ads)

    // Spotify SDK (place AARs in androidApp/libs/)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    // Testing
    testImplementation("junit:junit:4.13.2")
}
