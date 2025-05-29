plugins {
    id("plugins.android.library")
}

android {
    namespace = "com.sayler666.domain.model"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
