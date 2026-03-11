plugins {
    id("plugins.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sayler666.gina.data.network"
}

dependencies {
    implementation(libs.androidx.annotation.experimental)

    // Dagger hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.kotlin.serialization.json)

    // Other
    implementation(libs.timber)
}
