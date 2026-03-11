plugins {
    id("plugins.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.reminders"
}

dependencies {
    implementation(projects.core)
    implementation(projects.resources)
    implementation(projects.navigation)
    implementation(projects.dataDatabase)
    implementation(projects.domainModel)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // AndroidX
    implementation(libs.androidx.core)

    // Other
    implementation(libs.timber)
}
