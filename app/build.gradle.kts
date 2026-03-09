plugins {
    id("plugins.android.application")
    id("plugins.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(projects.domainModel)
    implementation(projects.featureSettings)
    implementation(projects.featureCalendar)
    implementation(projects.featureFriends)
    implementation(projects.featureInsights)
    implementation(projects.featureGallery)
    implementation(projects.featureDay)
    implementation(projects.featureJournal)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Kotlin
    implementation(libs.kotlin.serialization.json)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Android X
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.androidx.compose.material.icons)

    // Nav3
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.coil.compose)
    implementation(libs.calendar.compose)
    implementation(libs.richeditor.compose)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.okhttp3.logging.interceptor)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Other
    implementation(libs.timber)
    implementation(libs.haze)
    implementation(libs.haze.materials)

    testImplementation(libs.junit)
}
