<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# slang-intellij Changelog

## [Unreleased]

### Fixed

- Rider (and CLion Nova) no longer report the plugin as conflicting with the IDE at startup. The
  GLSL and HLSL file types and languages are now registered under the internal ids `SlangGLSL` /
  `SlangHLSL` instead of `GLSL` / `HLSL`, which collided with Rider's bundled HLSL support and the
  standalone GLSL plugin. The file types still show as "GLSL" / "HLSL" in the UI. Existing GLSL/HLSL
  extension associations are re-created automatically on first start.
- On CLion Nova and Rider, "Go to Declaration" via Cmd/Ctrl+B or Cmd/Ctrl+Click no longer reports
  "Cannot find declaration to go to". Those IDEs never sent the LSP `textDocument/definition`
  request from that action ([CPP-51642](https://youtrack.jetbrains.com/issue/CPP-51642)); the
  plugin now resolves it directly instead of relying on the platform's built-in navigation.

## [0.2.0] - 2026-09-04

This version is a ground-up rewrite. The hand-written parser and the optional LSP4IJ dependency are
gone; the plugin is now a thin layer over the official Slang language server (`slangd`) using the
JetBrains LSP client API.

### Added

- Local editor support that works without a language server: syntax highlighting (with a color
  settings page), brace matching, comment toggling, `"` auto-closing, Enter-key indentation,
  code folding for `{ }` blocks, block comments and `#if` / `#endif` regions, TODO / FIXME markers
  in the TODO tool window, and spell-checking of comment text.
- Highlight other occurrences of the identifier under the caret. When `slangd` is available the
  occurrences are grouped by the declaration it resolves (a local `foo` no longer lights up an
  unrelated field `foo`); otherwise it falls back to matching the name.
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
- **HLSL** support, on by default (a toggle under **Languages & Frameworks | Slang**): `.hlsl`,
  `.hlsli`, `.fx`, and HLSL-adjacent files like Unreal's `.usf` / `.ush` are handled by `slangd`,
  which parses HLSL as a superset. Optional **GLSL** support (off by default) does the same for
  `.vert` / `.frag` / `.comp` / `.glsl`, or a `.slang` file with a leading `#version`. Each only
  claims an extension no other plugin owns (so it is a no-op in Rider or with the GLSL plugin), and
  turning a toggle off releases exactly what it took.

### Changed

- Minimum IDE version is now 2026.2. Language-server features require a commercial IntelliJ-based
  IDE (IntelliJ IDEA, CLion, Rider, …); the local features work wherever the plugin loads.

### Known issues

- With `slangd` 2026.16.1 and earlier, some language-server requests (hovering certain keywords,
  and occurrence highlighting when the caret is on a declaration) produce an IDE error: `slangd`
  serialises an empty result as `{}` instead of `null`, which the LSP client cannot parse. Fixed
  upstream in `slangd`; the plugin picks it up with the next release. Occurrence highlighting falls
  back to matching the identifier name after the first such error.

## [0.1.0] - 2024-12-23

Last release built on the hand-written parser. See the `dev` branch history for its changelog.

[Unreleased]: https://github.com/Mcgode/slang-intellij/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/Mcgode/slang-intellij/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Mcgode/slang-intellij/commits/v0.1.0
