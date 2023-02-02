import Versions.Versions

object Deps {

    // Classpath Gradle Plugin
    val classpathGradle by lazy { "com.android.tools.build:gradle:${Versions.gradle}" }
    val classpathKotlinGradle by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val classpathKotlinSerialization by lazy { "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}" }
    val classpathHilt by lazy { "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}" }

    val hilt by lazy { "com.google.dagger:hilt-android:${Versions.hilt}" }
    val hiltCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hilt}" }

    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}" }
    val kotlinSerializationJson by lazy { "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerializationJson}" }
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val navigationKtx by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigation}" }
    val lifecycle by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}" }
    val lifecycleViewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}" }
    val lifecycleRuntimeCompose by lazy { "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}" }
    val lifecycleRuntime by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}" }
    val lifecycleCompiler by lazy { "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}" }
    val splashScreen by lazy { "androidx.core:core-splashscreen:${Versions.splashScreen}" }

    val composeUi by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
    val accompanistSystemUi by lazy { "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}" }
    val accompanistFlowLayout by lazy { "com.google.accompanist:accompanist-flowlayout:${Versions.accompanist}" }
    val composeConstraintLayout by lazy { "androidx.constraintlayout:constraintlayout-compose:${Versions.composeConstraintLayout}" }
    val composeIcons by lazy { "androidx.compose.material:material-icons-extended:${Versions.composeMaterialIcons}" }
    val composeMaterial3 by lazy { "androidx.compose.material3:material3:${Versions.composeMaterial3}" }
    val composeMaterial2 by lazy { "androidx.compose.material:material:${Versions.composeMaterial2}" }
    val composeNavigation by lazy { "androidx.navigation:navigation-compose:${Versions.navigation}" }
    val composeUiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityCompose}" }
    val hiltNavigationCompose by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigation}" }
    val composeDestination by lazy { "io.github.raamcosta.compose-destinations:animations-core:${Versions.composeDestination}" }
    val composeDestinationKsp by lazy { "io.github.raamcosta.compose-destinations:ksp:${Versions.composeDestination}" }
    val coilCompose by lazy { "io.coil-kt:coil-compose:${Versions.coilCompose}" }
    val calendarCompose by lazy { "com.kizitonwose.calendar:compose:${Versions.calendarCompose}" }

    val room by lazy { "androidx.room:room-runtime:${Versions.room}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.room}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.room}" }

    val dataStorePreferences by lazy { "androidx.datastore:datastore-preferences:${Versions.dataStorePreferences}" }
    val okio by lazy { "com.squareup.okio:okio:${Versions.okio}" }

    val commonsIo by lazy { "commons-io:commons-io:${Versions.commonsIo}" }
    val compressor by lazy { "id.zelory:compressor:${Versions.compressor}" }

    val desugarJdkLibs by lazy { "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}" }

    val timber by lazy { "com.jakewharton.timber:timber:${Versions.timber}" }
    val junit by lazy { "junit:junit:${Versions.jUnit}" }
}
