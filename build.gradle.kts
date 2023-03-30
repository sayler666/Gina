buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Deps.classpathGradle)
        classpath(Dependencies.Deps.classpathKotlinGradle)
        classpath(Dependencies.Deps.classpathHilt)
        classpath(Dependencies.Deps.classpathKotlinSerialization)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
