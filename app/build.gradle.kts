import ConfigData.ConfigData
import Dependencies.Deps
import Versions.Versions

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp") version "1.7.21-1.0.8"
}

android {
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
        getByName("release") {
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

        // For AGP 4.1+
        isCoreLibraryDesugaringEnabled = true
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
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
    implementation(Deps.composeMaterial)
    implementation(Deps.navigationKtx)
    implementation(Deps.composeNavigation)
    implementation(Deps.accompanistSystemUi)
    implementation(Deps.composeDestination)
    implementation(Deps.composeIcons)
    ksp(Deps.composeDestinationKsp)
    implementation(Deps.room)
    kapt(Deps.roomCompiler)
    implementation(Deps.roomKtx)
    implementation(Deps.timber)
    testImplementation(Deps.junit)
}
