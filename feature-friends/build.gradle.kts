plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.friends"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreUi)
    implementation(projects.resources)
    implementation(projects.domainModel)
    implementation(projects.dataDatabase)
    implementation(projects.navigation)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.constraintLayout.compose)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.coil.compose)

    // Other
    implementation(libs.timber)
}
