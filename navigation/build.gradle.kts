plugins {
    id("plugins.android.library")
    id("plugins.android.compose")
}

android {
    namespace = "com.sayler666.gina.navigation"
}

dependencies {
    implementation(projects.domainModel)
    api(libs.navigation3.ui)
}
