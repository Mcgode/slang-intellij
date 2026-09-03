# Slang Language Support for JetBrains IDEs

[![Build](https://github.com/Mcgode/slang-intellij/actions/workflows/build.yml/badge.svg)](https://github.com/Mcgode/slang-intellij/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/26040-slang-language-support.svg)](https://plugins.jetbrains.com/plugin/26040-slang-language-support)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26040-slang-language-support.svg)](https://plugins.jetbrains.com/plugin/26040-slang-language-support)

<!-- Plugin description -->
Editor support for the [Slang](https://shader-slang.com/) shading language.

Features integration of [`slangd`](https://github.com/shader-slang/slang), the official Slang language server, over LSP:

- Code completion, error and warning diagnostics, hover documentation
- Go to definition, signature help, semantic highlighting, formatting, inlay hints

The plugin also provides some added features beyond language-server integration:

- Offline syntax highlighting, with a dedicated color settings page
- Occurrence highlighting (with symbol resolution when `slangd` is enabled)
- Auto-indentation
- Brace and quote matching
- Comment toggling
- Code folding (`{ }` blocks, block comments, `#if` / `#endif`)


The plugin can download and manage a matching `slangd` for you, or use one from your `PATH` or a
path you configure. Everything is set up under **Settings | Languages & Frameworks | Slang** —
including per-project include search paths and predefined macros that are forwarded to `slangd`.

Slang's front-end is a near-superset of HLSL, so **HLSL** files — including HLSL-adjacent ones like
Unreal's `.usf` / `.ush` — get the same language-server features; this is **on by default**.
Optional **GLSL** support (`.vert` / `.frag` / `.comp` / `.glsl`, or a `.slang` file with a leading
`#version`) can be turned on in the same settings. In both cases the plugin only claims an
extension where no other plugin already owns it — so it is a no-op in Rider (built-in HLSL) or
alongside a dedicated GLSL plugin.

Language-server features need a JetBrains IDE that bundles the LSP client (IntelliJ IDEA, CLion,
Rider and other commercial IDEs, 2026.2 or newer). The local features work anywhere the plugin
loads.
<!-- Plugin description end -->

## Installation

- **Marketplace:** <kbd>Settings/Preferences</kbd> → <kbd>Plugins</kbd> → <kbd>Marketplace</kbd> →
  search for "Slang Language Support" → <kbd>Install</kbd>.
- **From disk:** download a release from the
  [Marketplace versions page](https://plugins.jetbrains.com/plugin/26040-slang-language-support/versions)
  or the [GitHub releases](https://github.com/Mcgode/slang-intellij/releases), then
  <kbd>Settings/Preferences</kbd> → <kbd>Plugins</kbd> → <kbd>⚙️</kbd> → <kbd>Install plugin from disk…</kbd>.

## The Slang language server

Language-server features require `slangd`. In **Settings | Languages & Frameworks | Slang** you choose:

- **Plugin-managed** (default) — the plugin downloads a pinned `slangd` from the Slang GitHub
  releases into the IDE cache (the large LLVM component is skipped, ~75 MB on disk). It offers to
  do this on first use, or automatically if you enable it.
- **System** — a `slangd` from a path you set, or found on your `PATH` (e.g. the one shipped with
  the Vulkan SDK under `$VULKAN_SDK/bin`).

The settings page shows the resolved path and version of each, and warns if the system `slangd` is
older than the version the plugin was built against.

## Development

| Task | |
|------|--|
| `./gradlew runIde` | sandbox IDE with the plugin installed |
| `./gradlew runIdeForTests` | same, with `testProject/` opened |
| `./gradlew test` | unit tests |
| `./gradlew koverHtmlReport` | coverage report under `build/reports/kover/html` |
| `./gradlew verifyPlugin` | IntelliJ Plugin Verifier |
| `./gradlew qodanaScan` | Qodana static analysis (needs Docker) |
| `./gradlew buildPlugin` | distributable zip under `build/distributions` |

The pre-2.0 hand-written parser and its test corpus live on the `dev` branch.

## A note on how this was built

The 0.2 rewrite was largely "vibe coded" with an AI assistant, though reviewed and steered by
practiced hands. Every change compiles, the unit tests pass, and the plugin clears the JetBrains
Plugin Verifier — but much less of it has been exercised by hand than a plugin this age normally
would be. If something misbehaves, please open an issue.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
