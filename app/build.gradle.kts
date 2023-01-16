import ConfigData.ConfigData
import Dependencies.Deps
import Versions.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.7.21-1.0.8"
}

val useReleaseKeystore = rootProject.file("release-keystore.jks").exists()

android {

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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        // For AGP 4.1+
        isCoreLibraryDesugaringEnabled = true
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"

            freeCompilerArgs += listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeKotlinCompiler
    }

    buildFeatures {
        compose = true
    }

    kapt {
        correctErrorTypes = true
    }

    hilt {
        enableAggregatingTask = true
    }

    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {
    coreLibraryDesugaring(Deps.desugarJdkLibs)
    implementation(Deps.hilt)
    implementation(Deps.hiltNavigationCompose)
    kapt(Deps.hiltCompiler)
    implementation(Deps.kotlin)
    implementation(Deps.appCompat)
    implementation(Deps.coreKtx)
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
    implementation(Deps.accompanistSystemUi)
    implementation(Deps.accompanistFlowLayout)
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
    implementation(Deps.okio)
    implementation(Deps.timber)
    testImplementation(Deps.junit)
}
