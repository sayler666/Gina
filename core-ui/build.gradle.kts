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

    // Kotlin Coroutines
    implementation(libs.kotlin.coroutines.core)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose extras
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.compose.destinations.core)
    implementation(libs.richeditor.compose)
    implementation(libs.accompanist.systemuicontroller)

    // Other
    implementation(libs.timber)
}
