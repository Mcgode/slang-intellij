import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask.FailureLevel

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.qodana")
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

        // Spellchecker (a platform product module); depended on optionally in plugin.xml so a
        // stripped IDE without it still loads.
        bundledModule("intellij.spellchecker")

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

    // Marketplace plugin signing. `certificate/chain.crt` is committed; `certificate/private.pem` is
    // gitignored (present locally). CI restores the key from the SLANG_LANGUAGE_SUPPORT_PRIVATE_KEY secret into that path
    // before signing. The key is not passphrase-protected, so `password` is left unset.
    signing {
        certificateChainFile = layout.projectDirectory.file("certificate/chain.crt")
        privateKey = providers.environmentVariable("SLANG_LANGUAGE_SUPPORT_PRIVATE_KEY")
            .orElse(providers.fileContents(layout.projectDirectory.file("certificate/private.pem")).asText)
    }

    publishing {
        token = providers.environmentVariable("SLANG_LANGUAGE_SUPPORT_PUBLISH_TOKEN")
        // A pre-release version like 0.2.0-alpha.1 publishes to the "alpha" channel; 0.2.0 to "default".
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }

    pluginVerification {
        ides {
            recommended()
        }

        // The plugin now only touches public, stable platform API, so fail on any regression
        // toward internal / deprecated / non-extendable API as well as outright breakage.
        failureLevel = listOf(
            FailureLevel.COMPATIBILITY_PROBLEMS,
            FailureLevel.INVALID_PLUGIN,
            FailureLevel.MISSING_DEPENDENCIES,
            FailureLevel.PLUGIN_STRUCTURE_WARNINGS,
            FailureLevel.SCHEDULED_FOR_REMOVAL_API_USAGES,
            FailureLevel.DEPRECATED_API_USAGES,
            FailureLevel.INTERNAL_API_USAGES,
            FailureLevel.NON_EXTENDABLE_API_USAGES,
            FailureLevel.OVERRIDE_ONLY_API_USAGES,
        )
    }
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

// Code coverage. `./gradlew koverHtmlReport` / `koverXmlReport`; the XML report is also produced as
// part of `check` for CI to pick up (build/reports/kover/report.xml).
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

// Static analysis. `./gradlew qodanaScan` runs the linter in Docker locally; CI uses the Qodana
// GitHub action. Inspection scope, profile and report options live in qodana.yaml.
qodana {
    cachePath = provider { file(".qodana").canonicalPath }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn(patchChangelog)
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

        // `./gradlew runClionForTests` — same plugin, run in a CLion sandbox instead of IDEA, to
        // reproduce/experiment with CLion Nova bugs (e.g. CPP-51642: textDocument/definition never
        // sent on Cmd+B / Ctrl+Click). Downloads CLion on first run. The plugin is still *compiled*
        // against the IntelliJ IDEA dependency above; only the runtime IDE changes. Build numbers
        // must line up — CLion 262.x matches platformVersion 2026.2.
        register("runClionForTests") {
            type = IntelliJPlatformType.CLion
            version = providers.gradleProperty("clionVersion")
            task {
                args(testProjectPath)
            }
        }

        // `./gradlew runRiderForTests` — same plugin, run in a Rider sandbox instead of IDEA.
        register("runRiderForTests") {
            type = IntelliJPlatformType.Rider
            version = providers.gradleProperty("riderVersion")
            task {
                args(testProjectPath)
            }
        }
    }
}
