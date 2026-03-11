plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.insights"
}

dependencies {
    implementation(projects.core)
    implementation(projects.coreUi)
    implementation(projects.domainModel)
    implementation(projects.dataDatabase)
    implementation(projects.featureFriends)
    implementation(projects.featureCalendar)
    implementation(projects.navigation)

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

    // Calendar
    implementation(libs.calendar.compose)

    // Other
    implementation(libs.timber)
    implementation(libs.haze)
}
