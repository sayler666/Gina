plugins {
    id("plugins.android.application")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sayler666.gina"
}

dependencies {
    api(projects.core)
    implementation(projects.resources)
    implementation(projects.navigation)
    implementation(projects.coreUi)
    implementation(projects.dataDatabase)
    implementation(projects.featureSettings)
    implementation(projects.featureCalendar)
    implementation(projects.featureFriends)
    implementation(projects.featureInsights)
    implementation(projects.featureGallery)
    implementation(projects.featureDay)
    implementation(projects.featureJournal)
    implementation(projects.featureReminders)
    implementation(projects.featureSetup)
    implementation(projects.dataNetwork)
    implementation(projects.featureGameOfLife)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Android X
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material.icons)

    // Nav3
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)

    // Compose
    implementation(libs.compose.material3)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Other
    implementation(libs.timber)
    implementation(libs.haze)

    testImplementation(libs.junit)
}
