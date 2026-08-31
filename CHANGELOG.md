<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# slang-intellij Changelog

## [Unreleased]

### Changed

- Complete rewrite around the official Slang language server (`slangd`). The hand-written parser and
  the LSP4IJ integration are replaced by the JetBrains LSP client API (`LspIntegrationProvider`).
- Minimum IDE version is now 2026.2.

### Added

- Local syntax highlighting, brace matching, comment toggling and code folding (lexer-based, no
  language server required).
- LSP integration: automatic `slangd` discovery from settings or `PATH`.
- Settings page under **Languages & Frameworks | Slang**.

## [0.1.0] - 2024-12-23

See the `dev` branch history for the pre-rewrite (hand-written parser) changelog.

[Unreleased]: https://github.com/Mcgode/slang-intellij/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/Mcgode/slang-intellij/commits/v0.1.0
