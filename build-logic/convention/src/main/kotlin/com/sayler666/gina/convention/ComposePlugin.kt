package com.sayler666.gina.convention

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlin-compose-compiler"))
        }

        with(extensions.getByType<BaseExtension>()) {
            with(buildFeatures) {
                compose = true
            }

            with(dependencies) {
                add("implementation", libs.findBundle("compose").get())
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            }
        }

        with(extensions.getByType<ComposeCompilerGradlePluginExtension>()) {
            includeSourceInformation = true
        }
    }
}
