import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

gradlePlugin {
    plugins {
        register("AndroidApplication") {
            id = "plugins.android.application"
            implementationClass = "com.sayler666.gina.convention.AndroidApplicationPlugin"
        }
        register("AndroidLibrary") {
            id = "plugins.android.library"
            implementationClass = "com.sayler666.gina.convention.AndroidLibraryPlugin"
        }
        register("Compose") {
            id = "plugins.android.compose"
            implementationClass = "com.sayler666.gina.convention.ComposePlugin"
        }
        register("AndroidSingleVariant") {
            id = "plugins.android.singleVariant"
            implementationClass = "com.sayler666.gina.convention.AndroidSingleVariantPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(libs.plgn.andrid.application)
    implementation(libs.plgn.andrid.library)
    implementation(libs.plgn.kotlin.compose.compiler)
    implementation(libs.plgn.kotlin)
    implementation(libs.firebase.crashlytics.gradle)
}
