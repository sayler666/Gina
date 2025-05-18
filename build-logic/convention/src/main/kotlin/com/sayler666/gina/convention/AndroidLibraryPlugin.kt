package com.sayler666.gina.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("agp-library"))
            apply(libs.findPlugin("kotlin-android"))
        }

        with(extensions.getByType<LibraryExtension>()) {
            compileSdkVersion = libs.findVersionString("compileSdk")

            with(defaultConfig) {
                minSdk = libs.findVersionInt("minSdk")
            }

            with(buildTypes) {
                getByName("debug") {
                    matchingFallbacks += "release"
                }
            }

            with(compileOptions) {
                sourceCompatibility = JDK_VERSION
                targetCompatibility = JDK_VERSION
            }

            with(buildFeatures) {
                buildConfig = true
            }
        }

        with(extensions.getByType<KotlinAndroidProjectExtension>()) {
            compilerOptions.jvmTarget = JVM_TARGET
        }

    }

}
