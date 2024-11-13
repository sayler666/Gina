package com.sayler666.gina.convention

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidSingleVariantPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(extensions.getByType<LibraryAndroidComponentsExtension>()) {
            beforeVariants {
                it.enable = it.name == "release"
            }
        }
    }
}
