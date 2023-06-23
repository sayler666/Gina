import ConfigData.ConfigData

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
}

android {
    namespace = ConfigData.applicationId + ".core"
    compileSdkVersion = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    val fileProviderName = "fileProvider"
    buildTypes {
        debug {
            signingConfig = signingConfigs["debug"]
            isShrinkResources = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val fileProviderAuthority = "${ConfigData.applicationId}.core.debug.$fileProviderName"
            manifestPlaceholders["fileProviderAuthority"] = fileProviderAuthority
            buildConfigField(
                "String",
                "FILE_PROVIDER_AUTHORITY",
                "\"$fileProviderAuthority\""
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val fileProviderAuthority = "${ConfigData.applicationId}.core.$fileProviderName"
            manifestPlaceholders["fileProviderAuthority"] = fileProviderAuthority
            buildConfigField(
                "String",
                "FILE_PROVIDER_AUTHORITY",
                "\"$fileProviderAuthority\""
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composecompiler.get()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    kapt {
        correctErrorTypes = true
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.kotlin.serialization.json)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.commons.io)
    implementation(libs.okio)
    implementation(libs.timber)
}
