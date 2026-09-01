<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# slang-intellij Changelog

## [Unreleased]

This version is a ground-up rewrite. The hand-written parser and the optional LSP4IJ dependency are
gone; the plugin is now a thin layer over the official Slang language server (`slangd`) using the
JetBrains LSP client API.

### Added

- Local editor support that works without a language server: syntax highlighting (with a color
  settings page), brace matching, comment toggling, and code folding for `{ }` blocks, block
  comments and `#if` / `#endif` regions.
- `slangd` integration over LSP: completion, diagnostics, hover, go to definition, signature help,
  semantic highlighting, formatting and inlay hints.
- `slangd` management: choose between a **plugin-managed** `slangd` (downloaded from the Slang
  GitHub releases, minus the LLVM component) and a **system** `slangd` from a configured path or
  `PATH`. The settings page shows each one's resolved path and version, and offers to download or
  update the managed copy.
- Notification when the system `slangd` is older than the version the plugin was built against,
  with a one-click switch to the managed copy.
- Settings under **Languages & Frameworks | Slang**: `slangd` source and path, automatic download,
  inlay-hint toggles, and per-project include search paths, predefined macros and
  "search all project directories", forwarded to `slangd`.
- Semantic tokens reported by `slangd` are mapped onto the plugin's own color keys, so the color
  settings page controls language-server highlighting too.

### Changed

- Minimum IDE version is now 2026.2. Language-server features require a commercial IntelliJ-based
  IDE (IntelliJ IDEA, CLion, Rider, …); the local features work wherever the plugin loads.

### Fixed

- `slangd` sometimes answers `textDocument/hover` with an empty (`null` contents) result, which
  crashed the platform's LSP hover handling. Such responses are now normalised to "no hover".

## [0.1.0] - 2024-12-23

Last release built on the hand-written parser. See the `dev` branch history for its changelog.

[Unreleased]: https://github.com/Mcgode/slang-intellij/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/Mcgode/slang-intellij/commits/v0.1.0
