# Slang plugin test project

A small, self-contained set of Slang shaders for exercising the plugin in a sandbox IDE.
Committed to the repo so testing is reproducible.

## Running

```bash
./gradlew runIdeForTests
```

This launches a sandbox IntelliJ IDEA (2026.2) with the plugin installed and this directory
opened as the project.

`slangd` must be discoverable for the language-server features. Either:
- put `slangd` on your `PATH` (e.g. from the Slang release tarball's `bin/`, or the Vulkan SDK), or
- set its path in **Settings | Languages & Frameworks | Slang** inside the sandbox IDE.

## What to check

| File | Exercises |
|------|-----------|
| `common/brdf.slangh` | `.slangh` extension mapping, `#define` / `#ifndef` folding, object + function-like macros |
| `common/lighting.slang` | module declaration, `interface`, generics (`<T : ILightModel>`), `public` |
| `shaders/mesh.slang` | `#include`, `import`, `cbuffer`/`ConstantBuffer`, HLSL semantics, `[shader(...)]`, texture sampling, cross-file completion into `lighting` |
| `shaders/particles.slang` | compute shader, `[numthreads]`, `RWStructuredBuffer`, `SV_DispatchThreadID` |
| `shaders/postprocess.slang` | `static const` array, `SV_VertexID`, swizzles, fullscreen-triangle idiom |
| `errors/diagnostics.slang` | **intentionally broken** — diagnostics should show squiggles + Problems entries |

Local (no server) features to verify: syntax highlighting, brace matching, comment toggle
(<kbd>Cmd/Ctrl</kbd>+<kbd>/</kbd>), folding of `{ }` blocks / block comments / `#if…#endif`.

Server features to verify: completion (incl. members after `.` and across `import`), hover,
signature help, go-to-definition, diagnostics, semantic highlighting, formatting, inlay hints.
