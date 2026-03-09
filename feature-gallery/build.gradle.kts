plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.gallery"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreUi)
    implementation(projects.resources)
    implementation(projects.domainModel)
    implementation(projects.dataDatabase)
    implementation(projects.navigation)

    // Navigation3
    implementation(libs.navigation3.ui)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.coil.compose)

    // Haze
    implementation(libs.haze)

    // Other
    implementation(libs.timber)
}
