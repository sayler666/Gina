plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "com.sayler666.gina.feature.journal"
}

dependencies {
    implementation(projects.core)
    implementation(projects.resources)
    implementation(projects.coreUi)
    implementation(projects.dataDatabase)
    implementation(projects.navigation)
    implementation(projects.domainModel)
    implementation(projects.featureSettings)
    implementation(projects.featureDay)

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
    implementation(libs.androidx.compose.material.icons)

    // Compose
    implementation(libs.compose.material3)

    // Coil
    implementation(libs.coil.compose)

    // Other
    implementation(libs.timber)
    implementation(libs.haze)
    implementation(libs.kotlin.collections.immutable)
}
