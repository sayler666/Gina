plugins {
    id("plugins.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sayler666.gina.data.database"
}

dependencies {
    implementation(projects.core)
    implementation(projects.domainModel)

    // Dagger
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Datastore
    implementation(libs.androidx.datastore)

    // Other
    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
