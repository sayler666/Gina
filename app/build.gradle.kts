import ConfigData.ConfigData
import Dependencies.Deps

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
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
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
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
}

dependencies {
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
    implementation(Deps.constraintLayout)
    implementation(Deps.composeMaterial)
    implementation(Deps.navigationKtx)
    implementation(Deps.composeNavigation)
    implementation(Deps.room)
    kapt(Deps.roomCompiler)
    implementation(Deps.roomKtx)
    testImplementation(Deps.junit)
}
