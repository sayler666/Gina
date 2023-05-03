import ConfigData.ConfigData
import Dependencies.Deps
import Versions.Versions

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("kotlinx-serialization")
}

val useReleaseKeystore = rootProject.file("release-keystore.jks").exists()

android {
    namespace = "com.sayler666.gina"

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("release-keystore.jks")
            storePassword = "splurge-bakery-pardon"
            keyAlias = "gina3"
            keyPassword = "splurge-bakery-pardon"
        }
    }

    buildToolsVersion = ConfigData.buildToolsVersion
    compileSdkVersion = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = ConfigData.applicationId
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName
        minSdk = ConfigData.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled = true
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs["debug"]
            isShrinkResources = false
            isMinifyEnabled = false
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".debug"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            signingConfig = signingConfigs[if (useReleaseKeystore) "release" else "debug"]
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        // For AGP 4.1+
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(17)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeKotlinCompiler
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kapt {
        correctErrorTypes = true
    }

    hilt {
        enableAggregatingTask = true
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(
            File(buildDir, "generated/ksp/$name/kotlin")
        )
    }
}

dependencies {
    coreLibraryDesugaring(Deps.desugarJdkLibs)
    implementation(Deps.hilt)
    implementation(Deps.hiltNavigationCompose)
    kapt(Deps.hiltCompiler)
    implementation(Deps.kotlin)
    implementation(Deps.kotlinSerializationJson)
    implementation(Deps.appCompat)
    implementation(Deps.activityCompose)
    implementation(Deps.composeUi)
    implementation(Deps.composeUiToolingPreview)
    implementation(Deps.lifecycle)
    implementation(Deps.lifecycleRuntimeCompose)
    implementation(Deps.lifecycleViewModelCompose)
    implementation(Deps.lifecycleRuntime)
    implementation(Deps.lifecycleCompiler)
    implementation(Deps.timber)
    implementation(Deps.composeMaterial3)
    implementation(Deps.composeMaterial2)
    implementation(Deps.navigationKtx)
    implementation(Deps.composeNavigation)
    implementation(Deps.composeConstraintLayout)
    implementation(Deps.accompanistSystemUi)
    implementation(Deps.composeDestination)
    implementation(Deps.coilCompose)
    implementation(Deps.composeIcons)
    ksp(Deps.composeDestinationKsp)
    implementation(Deps.room)
    kapt(Deps.roomCompiler)
    implementation(Deps.roomKtx)
    implementation(Deps.dataStorePreferences)
    implementation(Deps.commonsIo)
    implementation(Deps.calendarCompose)
    implementation(Deps.splashScreen)
    implementation(Deps.compressor)
    implementation(Deps.okio)
    implementation(Deps.retrofit)
    implementation(Deps.retrofitConverterMoshi)
    implementation(Deps.moshi)
    implementation(Deps.moshiKotlin)
    implementation(Deps.timber)
    testImplementation(Deps.junit)
}
