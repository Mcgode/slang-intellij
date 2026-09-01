import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask.FailureLevel

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(21)
    compilerOptions {
        // IntelliJ IDEA 2026.2 bundles Kotlin libraries with a newer metadata version than the
        // Kotlin Gradle plugin we build with; the plugin only touches stable platform API.
        freeCompilerArgs.addAll("-Xskip-metadata-version-check", "-Xskip-prerelease-check")
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(libs.junit)

    intellijPlatform {
        // Unified IntelliJ IDEA distribution (bundles the LSP client API).
        intellijIdea(providers.gradleProperty("platformVersion"))

        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }

    pluginVerification {
        ides {
            recommended()
        }

        // The lsp4j hover-response workaround (SlangLspServerCustomization) has to go through
        // Lsp4jServerWrapper / LspClientManager.addLsp4jServerWrapper, which are @ApiStatus.Internal
        // and take the deprecated LspServer type — there is no public equivalent. Keep those two
        // categories reported but non-fatal; still fail on everything that actually breaks users.
        failureLevel = listOf(
            FailureLevel.COMPATIBILITY_PROBLEMS,
            FailureLevel.INVALID_PLUGIN,
            FailureLevel.MISSING_DEPENDENCIES,
            FailureLevel.PLUGIN_STRUCTURE_WARNINGS,
            FailureLevel.SCHEDULED_FOR_REMOVAL_API_USAGES,
        )
    }
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}

// `./gradlew runIdeForTests` — sandbox IDE with the bundled testProject/ opened.
val testProjectPath: String = layout.projectDirectory.dir("testProject").asFile.absolutePath

intellijPlatformTesting {
    runIde {
        register("runIdeForTests") {
            task {
                args(testProjectPath)
            }
        }
    }
}
