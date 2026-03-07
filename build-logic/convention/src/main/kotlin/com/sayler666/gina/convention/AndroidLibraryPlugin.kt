package com.sayler666.gina.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("agp-library"))
        }

        val compileSdkVersion = libs.findVersionInt("compileSdk")
        val minSdkVersion = libs.findVersionInt("minSdk")

        extensions.configure<LibraryExtension> {
            compileSdk = compileSdkVersion

            with(defaultConfig) {
                minSdk = minSdkVersion
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
