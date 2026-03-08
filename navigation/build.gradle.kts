plugins {
    id("plugins.android.library")
}

android {
    namespace = "com.sayler666.gina.navigation"
}

dependencies {
    implementation(projects.domainModel)
}
