import Versions.Versions

object Deps {

    // Classpath Gradle Plugin
    val classpathGradle by lazy { "com.android.tools.build:gradle:${Versions.gradle}" }
    val classpathKotlinGradle by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val classpathHilt by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}" }

    val hilt by lazy { "com.google.dagger:hilt-android:${Versions.hilt}" }
    val hiltCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hilt}" }

    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}" }
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val coreKtx by lazy { "androidx.core:core-ktx:1.7.0" }
    val navigationKtx by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigation}" }
    val lifecycle by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" }
    val lifecycleViewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}" }
    val lifecycleRuntimeCompose by lazy { "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}" }
    val lifecycleRuntime by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}" }
    val lifecycleCompiler by lazy { "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}" }

    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
    val composeMaterial by lazy { "androidx.compose.material3:material3:${Versions.composeMaterial3}" }
    val composeNavigation by lazy { "androidx.navigation:navigation-compose:${Versions.navigation}" }
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val hiltNavigationCompose by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigation}" }
    val composeDestination by lazy { "io.github.raamcosta.compose-destinations:core:${Versions.composeDestination}" }
    val composeDestinationKsp by lazy { "io.github.raamcosta.compose-destinations:ksp:${Versions.composeDestination}" }

    val room by lazy { "androidx.room:room-runtime:${Versions.room}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.room}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.room}" }

    val timber by lazy { "com.jakewharton.timber:timber:${Versions.timber}" }
    val junit by lazy { "junit:junit:${Versions.jUnit}" }
}
