package com.sayler666.gina.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlin-compose-compiler"))
        }

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                buildFeatures.compose = true
            }
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                buildFeatures.compose = true
            }
        }

        dependencies.add("implementation", dependencies.platform(libs.findLibrary("compose-bom").get()))
        dependencies.add("implementation", libs.findBundle("compose").get())
        dependencies.add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())

        extensions.getByType<ComposeCompilerGradlePluginExtension>().includeSourceInformation = true
    }
}
