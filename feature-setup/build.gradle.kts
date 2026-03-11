plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.setup"
}

dependencies {
    implementation(projects.core)
    implementation(projects.resources)
    implementation(projects.coreUi)
    implementation(projects.dataDatabase)
    implementation(projects.navigation)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Navigation3
    implementation(libs.navigation3.ui)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose
    implementation(libs.compose.material3)
}
