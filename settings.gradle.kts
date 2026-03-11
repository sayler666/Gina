include(":domain-model")
include(":resources")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

rootProject.name = "Gina"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core")
include(":core-ui")
include(":data-database")
include(":data-network")
include(":feature-settings")
include(":feature-calendar")
include(":feature-friends")
include(":feature-insights")
include(":feature-gallery")
include(":feature-day")
include(":feature-journal")
include(":feature-reminders")
include(":navigation")
