plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
}

android {
    namespace = "com.sayler666.gina.core.ui"
}

dependencies {
    implementation(projects.core)
    implementation(projects.domainModel)
    implementation(projects.dataDatabase)
    implementation(projects.navigation)

    // Kotlin Coroutines
    implementation(libs.kotlin.coroutines.core)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation3
    implementation(libs.navigation3.ui)

    // Compose extras
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.richeditor.compose)
    // Other
    implementation(libs.timber)
    implementation(libs.haze)
}
