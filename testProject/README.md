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
| `common/brdf.slang` | `.slangh` extension mapping, `#define` / `#ifndef` folding, object + function-like macros |
| `common/lighting.slang` | module declaration, `interface`, generics (`<T : ILightModel>`), `public` |
| `shaders/mesh.slang` | `#include`, `import`, `cbuffer`/`ConstantBuffer`, HLSL semantics, `[shader(...)]`, texture sampling, cross-file completion into `lighting` |
| `shaders/particles.slang` | compute shader, `[numthreads]`, `RWStructuredBuffer`, `SV_DispatchThreadID` |
| `shaders/postprocess.slang` | `static const` array, `SV_VertexID`, swizzles, fullscreen-triangle idiom |
| `errors/diagnostics.slang` | **intentionally broken** — diagnostics should show squiggles + Problems entries |

### GLSL (`glsl/`)

`depthonly.slang` works out of the box: slangd parses it as GLSL because of its leading `#version`
line. The `.vert` / `.frag` / `.comp` files need **Settings | Languages & Frameworks | Slang →
Handle GLSL files** turned on first; then they get the same language-server support.

| File | Exercises |
|------|-----------|
| `glsl/depthonly.slang` | GLSL body with a leading `#version` in a `.slang` file — full support, GLSL toggle **off** |
| `glsl/mesh.vert` | vertex stage, UBO + `push_constant`, `mat3(mat4)`, swizzles, `gl_Position` |
| `glsl/triangle.frag` | minimal fragment stage, `push_constant`, `gl_` builtins |
| `glsl/lighting.frag` | `struct`, UBO array, helper function, `for` loop, `sampler2D` / `texture()` — folding + occurrence highlighting |
| `glsl/post.frag` | `layout(constant_id)` spec constant, `const`, multiple functions, `switch` |
| `glsl/particles.comp` | compute stage, `layout(local_size_x)`, `std430 buffer` SSBO, `gl_GlobalInvocationID` |
| `glsl/brdf.glslh` | GLSL **header** — `#ifndef` include guard folding, helper functions; opened alone it has no `#version` so expect squiggles |
| `glsl/pbr.frag` | `#include "brdf.glslh"` via `GL_GOOGLE_include_directive` — cross-file completion / go-to-definition into the header |
| `glsl/broken.frag` | **intentionally broken** — undeclared identifier + type mismatch |

Local (no server) features to verify: syntax highlighting, brace matching, comment toggle
(<kbd>Cmd/Ctrl</kbd>+<kbd>/</kbd>), folding of `{ }` blocks / block comments / `#if…#endif`.

Server features to verify: completion (incl. members after `.` and across `import`), hover,
signature help, go-to-definition, diagnostics, semantic highlighting, formatting, inlay hints.
