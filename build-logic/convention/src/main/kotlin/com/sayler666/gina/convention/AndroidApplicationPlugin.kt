package com.sayler666.gina.convention

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import java.io.File

class AndroidApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins()

        application()

        with(extensions.getByType<KotlinAndroidProjectExtension>()) {
            compilerOptions.jvmTarget = JVM_TARGET
        }

        buildVersionFile()

    }

    private fun Project.buildVersionFile() {
        tasks.register("buildVersionFile") {
            val versionFilePath = rootProject.file("version.txt").absolutePath
            val version = getAppVersionName()
            doLast {
                File(versionFilePath).writeText(version)
            }
        }
    }

    private fun Project.plugins() = with(pluginManager) {
        apply(libs.findPlugin("agp-application"))
        apply(libs.findPlugin("kotlin-android"))
        apply(libs.findPlugin("google-services"))
        apply(libs.findPlugin("firebase-crashlytics"))
    }

    private fun Project.application() = with(extensions.getByType<AppExtension>()) {
        val useReleaseKeystore = rootProject.file("release-keystore.jks").exists()
        compileSdkVersion = libs.findVersionString("compileSdk")

        with(defaultConfig) {
            applicationId = "com.sayler666.gina"
            minSdk = libs.findVersionInt("minSdk")
            targetSdk = libs.findVersionInt("targetSdk")
            versionCode = libs.findVersionInt("versionCode")
            versionName = getAppVersionName()
        }

        with(compileOptions) {
            sourceCompatibility = JDK_VERSION
            targetCompatibility = JDK_VERSION

            isCoreLibraryDesugaringEnabled = true
        }

        with(buildFeatures) {
            buildConfig = true
        }

        packagingOptions.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

        with(signingConfigs) {
            create("release") {
                storeFile = rootProject.file("release-keystore.jks")
                storePassword = "splurge-bakery-pardon"
                keyAlias = "gina3"
                keyPassword = "splurge-bakery-pardon"
            }
        }

        with(buildTypes) {
            getByName("release") {
                signingConfig = signingConfigs[if (useReleaseKeystore) "release" else "debug"]
                isMinifyEnabled = true
                isShrinkResources = true
                isDebuggable = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                manifestPlaceholders["crashlytics_enabled"] = true
            }
            getByName("debug") {
                isDefault = true
                versionNameSuffix = "-dev"
                applicationIdSuffix = ".debug"
                signingConfig = signingConfigs["debug"]
                isMinifyEnabled = false
                isShrinkResources = false
                isDebuggable = true
                matchingFallbacks += "release"
                manifestPlaceholders["crashlytics_enabled"] = false
            }
        }
    }

}
