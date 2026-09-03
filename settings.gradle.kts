import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

rootProject.name = "slang-intellij"

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.4.10"
        id("org.jetbrains.changelog") version "2.5.0"
        id("org.jetbrains.kotlinx.kover") version "0.9.9"
        id("org.jetbrains.qodana") version "2026.2.1"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.intellij.platform.settings") version "2.18.1"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        intellijPlatform {
            defaultRepositories()
        }
    }
}
