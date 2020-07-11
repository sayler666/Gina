plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}
android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.sayler.gina3"
        minSdkVersion(29)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    androidExtensions {
        isExperimental = true
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libs.kotlin_stdlib)

    // Core
    implementation(Libs.appcompat)

    // KTX
    implementation(Libs.activity_ktx)
    implementation(Libs.core_ktx)
    implementation(Libs.fragment_ktx)

    // UI
    implementation(Libs.constraintlayout)

    // Navigation component
    implementation(Libs.navigation_runtime_ktx)
    implementation(Libs.navigation_fragment_ktx)
    implementation(Libs.navigation_ui_ktx)

    // Lifecycle
    implementation(Libs.lifecycle_viewmodel_ktx)
    implementation(Libs.lifecycle_livedata_ktx)
    implementation(Libs.lifecycle_common_java8)

    // Room
    implementation(Libs.room_runtime)
    kapt(Libs.room_compiler)

    // Dagger
    implementation(Libs.hilt_android)
    kapt(Libs.hilt_android_compiler)
    implementation(Libs.hilt_lifecycle_viewmodel)
    kapt(Libs.hilt_compiler)

    // Coroutine
    implementation(Libs.kotlinx_coroutines_android)
    implementation(Libs.kotlinx_coroutines_core)

    // Moshi
    implementation(Libs.moshi)
    implementation(Libs.moshi_adapters)
    kapt(Libs.moshi_kotlin_codegen)

    testImplementation(Libs.junit_junit)
    androidTestImplementation(Libs.androidx_test_ext_junit)
    androidTestImplementation(Libs.espresso_core)

    implementation(project(":data"))
}