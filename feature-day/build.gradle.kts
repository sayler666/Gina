plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina.feature.day"
}

dependencies {
    implementation(projects.core)
    implementation(projects.resources)
    implementation(projects.featureReminders)
    implementation(projects.coreUi)
    implementation(projects.domainModel)
    implementation(projects.dataDatabase)
    implementation(projects.dataNetwork)
    implementation(projects.navigation)
    implementation(projects.featureFriends)
    implementation(projects.featureSettings)
    implementation(projects.featureCalendar)

    // Navigation3
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.constraintLayout.compose)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.coil.compose)
    implementation(libs.richeditor.compose)
    implementation(libs.haze)

    // Other
    implementation(libs.timber)
}
