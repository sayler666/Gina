import ConfigData.ConfigData

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization)
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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

}

dependencies {
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.kotlin.serialization.json)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.dagger.hilt)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.jsoup)
    implementation(libs.commons.io)
    implementation(libs.okio)
    implementation(libs.timber)
}
