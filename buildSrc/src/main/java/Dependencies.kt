package com.sayler.buildsrc

object App {
    const val versionName = "0.9.0"
    const val versionCode = 9000
    const val applicationId = "com.sayler.gina2"

    const val minSdk = 26
    const val targetSdk = 28
    const val compileSdk = 28
}

object Libs {

    object Kotlin {
        const val kotlinVersion = "1.3.41"
        const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    }

    object AndroidX {

        private const val coreKtxVersion = "1.0.2"
        const val coreKtx = "androidx.core:core-ktx:$coreKtxVersion"

        object Navigation {
            private const val version = "2.1.0-alpha05"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Paging {
            private const val version = "2.1.0"
            const val common = "androidx.paging:paging-common:$version"
            const val runtime = "androidx.paging:paging-runtime:$version"
            const val rxjava2 = "androidx.paging:paging-rxjava2:$version"
        }

        object Room {
            private const val version = "2.1.0"
            const val common = "androidx.room:room-common:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val rxjava2 = "androidx.room:room-rxjava2:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
        }

    }

    object Airbnb {
        const val mvrx = "com.airbnb.android:mvrx:1.0.2"
        const val epoxy = "com.airbnb.android:epoxy:3.7.0"
    }

    object Rx {
        const val rxJava = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val rxAndroid = "io.reactivex.rxjava2:rxjava:2.2.10"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
    }

    object Moshi {
        private const val moshiVersion = "1.8.0"
        const val moshi = "com.squareup.moshi:moshi:$moshiVersion"
        const val moshiAdapters = "com.squareup.moshi:moshi-adapters:$moshiVersion"
        const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"
    }

    object Dagger {
        private const val daggerVersion = "2.23.2"
        private const val assistedInjectVersion = "0.4.0"
        const val dagger = "com.google.dagger:dagger:$daggerVersion"
        const val daggerCompiler = "com.google.dagger:dagger-compiler:$daggerVersion"
        const val daggerAndroid = "com.google.dagger:dagger-android-support:$daggerVersion"
        const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:$daggerVersion"
        const val assistedInject = "com.squareup.inject:assisted-inject-annotations-dagger2:$assistedInjectVersion"
        const val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:$assistedInjectVersion"
    }
}
