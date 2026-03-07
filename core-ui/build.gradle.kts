plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
}

android {
    namespace = "com.sayler666.gina.core.ui"
}

dependencies {
    implementation(projects.core)

    // Kotlin Coroutines
    implementation(libs.kotlin.coroutines.core)

    // AndroidX
    implementation(libs.androidx.lifecycle.runtime.compose)
}
