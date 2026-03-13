package com.sayler666.gina.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
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
        apply(libs.findPlugin("google-services"))
        apply(libs.findPlugin("firebase-crashlytics"))
    }

    private fun Project.application() {
        val useReleaseKeystore = rootProject.file("release-keystore.jks").exists()
        val appVersionName = getAppVersionName()
        val compileSdkVersion = libs.findVersionInt("compileSdk")
        val minSdkVersion = libs.findVersionInt("minSdk")
        val targetSdkVersion = libs.findVersionInt("targetSdk")
        val versionCodeValue = libs.findVersionInt("versionCode")

        // Read signing credentials from environment variables / Gradle properties with local fallback
        val releaseKeystorePassword = getSigningCredential(
            envVar = "RELEASE_KEYSTORE_PASSWORD",
            gradleProperty = "release_keystore_password",
            localFallback = "splurge-bakery-pardon"
        )
        val releaseKeyAlias = getSigningCredential(
            envVar = "RELEASE_KEY_ALIAS",
            gradleProperty = "release_key_alias",
            localFallback = "gina3"
        )
        val releaseKeyPassword = getSigningCredential(
            envVar = "RELEASE_KEY_PASSWORD",
            gradleProperty = "release_key_password",
            localFallback = "splurge-bakery-pardon"
        )

        extensions.configure<ApplicationExtension> {
            compileSdk = compileSdkVersion

            with(defaultConfig) {
                applicationId = "com.sayler666.gina"
                minSdk = minSdkVersion
                targetSdk = targetSdkVersion
                versionCode = versionCodeValue
                versionName = appVersionName
            }

            with(compileOptions) {
                sourceCompatibility = JDK_VERSION
                targetCompatibility = JDK_VERSION
                isCoreLibraryDesugaringEnabled = true
            }

            with(buildFeatures) {
                buildConfig = true
            }

            packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

            with(signingConfigs) {
                create("release") {
                    storeFile = rootProject.file("release-keystore.jks")
                    storePassword = releaseKeystorePassword
                    keyAlias = releaseKeyAlias
                    keyPassword = releaseKeyPassword
                }
            }

            with(buildTypes) {
                getByName("release") {
                    signingConfig = signingConfigs.getByName(if (useReleaseKeystore) "release" else "debug")
                    isMinifyEnabled = true
                    isShrinkResources = true
                    isDebuggable = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
                getByName("debug") {
                    isDefault = true
                    versionNameSuffix = "-dev"
                    applicationIdSuffix = ".debug"
                    signingConfig = signingConfigs.getByName("debug")
                    isMinifyEnabled = false
                    isShrinkResources = false
                    isDebuggable = true
                    matchingFallbacks += "release"
                }
            }
        }
    }

    /**
     * Reads a signing credential from environment variable, Gradle property, or local fallback.
     * Priority: Environment variable > Gradle property > Local fallback
     *
     * @param envVar The environment variable name (e.g., "RELEASE_KEYSTORE_PASSWORD")
     * @param gradleProperty The Gradle project property name (e.g., "release_keystore_password")
     * @param localFallback The local fallback value (for development builds only)
     * @return The credential value
     */
    private fun Project.getSigningCredential(
        envVar: String,
        gradleProperty: String,
        localFallback: String
    ): String {
        return System.getenv(envVar)
            ?: findProperty(gradleProperty)?.toString()
            ?: localFallback
    }

}
