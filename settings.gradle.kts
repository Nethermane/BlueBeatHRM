pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "${extra["project.android.gradle.plugin.version"]}" apply false
        id("com.android.library") version "${extra["project.android.gradle.plugin.version"]}" apply false
        id("org.jetbrains.kotlin.android") version "${extra["project.kotlin.version"]}" apply false
        id("org.gradle.kotlin.kotlin-dsl") version "4.0.1"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "BlueBeatHRM"
include(":app")
include(":modules:core:core-bluetooth-connection")
include(":modules:core:core-bluetooth-scanner-ui")


//include(":modules:external:androidx-compose-material3-pullrefresh")

includeBuild("modules/external/androidx-compose-material3-pullrefresh") {
    dependencySubstitution {
        substitute(module("me.omico.lux:lux-androidx-compose-material3-pullrefresh")).using(
            project(
                ":library"
            )
        )
    }
}
