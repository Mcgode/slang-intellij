# Slang Language Support for JetBrains IDEs

![Build](https://github.com/Mcgode/slang-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26040-slang-language-support.svg)](https://plugins.jetbrains.com/plugin/26040-slang-language-support)

<!-- Plugin description -->
Editor support for the [Slang](https://shader-slang.com/) shading language.

Version 0.2 is a ground-up rewrite around the official Slang language server (`slangd`):

- Syntax highlighting, brace matching, comment toggling and code folding, provided locally by the plugin.
- Code completion, diagnostics, hover documentation, go-to-definition, signature help, semantic
  highlighting, formatting and inlay hints, provided by `slangd` over the Language Server Protocol.

The plugin locates `slangd` from its settings, then from your `PATH`. LSP features require a JetBrains
IDE with the bundled LSP client (IntelliJ IDEA, CLion, Rider and other commercial IDEs, 2026.2+).
<!-- Plugin description end -->

## Development

`./gradlew runIde` launches a sandbox IDE with the plugin installed. `./gradlew buildPlugin` produces
the distributable zip under `build/distributions`.

The pre-2.0 hand-written parser lives on the `dev` branch.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
