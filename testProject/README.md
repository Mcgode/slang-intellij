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
| `shaders/postprocess.slang` | `static const` array, `SV_VertexID`, swizzles, fullscreen-triangle idiom; a deliberate typo in the header comment for the spell-checker |
| `errors/diagnostics.slang` | **intentionally broken** — diagnostics should show squiggles + Problems entries |

### GLSL (`glsl/`)

The `.vert` / `.frag` / `.comp` / `.glsl` files need **Settings | Languages & Frameworks | Slang →
Handle GLSL files** turned on first; then they get full language-server support. `depthonly.slang`
works with the toggle off — a `.slang` file is treated as GLSL when it opens with a `#version` line.

| File | Exercises |
|------|-----------|
| `glsl/depthonly.slang` | GLSL body with a leading `#version` in a `.slang` file — full support, GLSL toggle **off** |
| `glsl/mesh.vert` | vertex stage, UBO + `push_constant`, `mat3(mat4)`, swizzles, `gl_Position` |
| `glsl/triangle.frag` | minimal fragment stage, `push_constant`, `gl_` builtins |
| `glsl/lighting.frag` | `struct`, UBO array, helper function, `for` loop, `sampler2D` / `texture()` — folding + occurrence highlighting |
| `glsl/post.frag` | `layout(constant_id)` spec constant, `const`, multiple functions, `switch` |
| `glsl/particles.comp` | compute stage, `layout(local_size_x)`, `std430 buffer` SSBO, `gl_GlobalInvocationID` |
| `glsl/brdf.glsl` | shared helpers in a `.glsl` file (no `#version`) — `#ifndef` guard folding, full support standalone |
| `glsl/pbr.frag` | `#include "brdf.glsl"` via `GL_GOOGLE_include_directive` — cross-file completion / go-to-definition into the header |
| `glsl/broken.frag` | **intentionally broken** — undeclared identifier + type mismatch |

### HLSL (`hlsl/`)

Needs **Settings | Languages & Frameworks | Slang → Handle HLSL files** turned on. Slang parses
HLSL as a superset, so these need no in-file marker.

| File | Exercises |
|------|-----------|
| `hlsl/mesh.hlsl` | `cbuffer` + `register`, `Texture2D` / `SamplerState`, HLSL semantics, `mul`, `(float3x3)` cast, two entry points in one file |
| `hlsl/common.hlsli` | `.hlsli` header — `#ifndef` guard folding, `struct`, `static const`, helper function; full support standalone |
| `hlsl/lit.hlsl` | `#include "common.hlsli"`, `StructuredBuffer<Light>`, `for` loop — cross-file completion / go-to-definition into the header |
| `hlsl/reduce.hlsl` | compute shader, `[numthreads]`, `groupshared`, `GroupMemoryBarrierWithGroupSync`, `SV_DispatchThreadID` / `SV_GroupIndex` |
| `hlsl/Desaturate.usf` | Unreal `.usf` extension → HLSL file type; note the *warning* on the bare `float` global (Slang flags it as an implicit shader parameter) |
| `hlsl/broken.hlsl` | **intentionally broken** — misspelled sampler + type mismatch |

Local (no server) features to verify: syntax highlighting, brace matching, comment toggle
(<kbd>Cmd/Ctrl</kbd>+<kbd>/</kbd>), folding of `{ }` blocks / block comments / `#if…#endif`, the
`TODO` in `shaders/particles.slang` in the TODO tool window, and the typo squiggle on "traingle" in
`shaders/postprocess.slang` (with `gHdrColor` / `SV_VertexID` left alone).

Server features to verify: completion (incl. members after `.` and across `import`), hover,
signature help, go-to-definition, diagnostics, semantic highlighting, formatting, inlay hints.
