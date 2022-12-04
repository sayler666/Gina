import Versions.Versions

object Deps {

    // Classpath Gradle Plugin
    val classpathGradle by lazy { "com.android.tools.build:gradle:${Versions.gradle}" }
    val classpathKotlinGradle by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val classpathHilt by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}" }

    val lifecycle by lazy {"androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"}
    val lifecycleViewModelCompose by lazy {"androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"}
    val lifecycleRuntimeCompose by lazy {"androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}"}
    val lifecycleRuntime by lazy {"androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"}
    val lifecycleCompiler by lazy {"androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"}

    val hilt by lazy {"com.google.dagger:hilt-android:${Versions.hilt}"}
    val hiltCompiler by lazy {"com.google.dagger:hilt-android-compiler:${Versions.hilt}"}
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val timber by lazy { "com.jakewharton.timber:timber:${Versions.timber}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}" }
    val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintLayoutCompose}" }
    val junit by lazy { "junit:junit:${Versions.jUnit}" }
    val coreKtx by lazy { "androidx.core:core-ktx:1.7.0" }
    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
    val composeMaterial by lazy { "androidx.compose.material:material:${Versions.composeMaterial}" }
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
}
