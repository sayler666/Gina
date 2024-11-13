plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sayler666.gina.core"

    val fileProviderName = "fileProvider"
    buildTypes {
        debug {
            val fileProviderAuthority = "com.sayler666.gina.core.debug.$fileProviderName"
            manifestPlaceholders["fileProviderAuthority"] = fileProviderAuthority
            buildConfigField(
                "String",
                "FILE_PROVIDER_AUTHORITY",
                "\"$fileProviderAuthority\""
            )
        }
        release {
            val fileProviderAuthority = "com.sayler666.gina.core.$fileProviderName"
            manifestPlaceholders["fileProviderAuthority"] = fileProviderAuthority
            buildConfigField(
                "String",
                "FILE_PROVIDER_AUTHORITY",
                "\"$fileProviderAuthority\""
            )
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Dagger
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt)

    // Kotlin
    implementation(libs.kotlin.serialization.json)

    // Kotlin Coroutines
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.datastore)

    // Other
    implementation(libs.timber)
    implementation(libs.jsoup)
    implementation(libs.commons.io)
    implementation(libs.okio)
}
