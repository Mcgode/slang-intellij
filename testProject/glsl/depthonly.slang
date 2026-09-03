#version 450

// A GLSL shader kept with a .slang extension. slangd parses it as GLSL because of the leading
// #version directive, so it has full language-server support even with GLSL support turned OFF.
// (Do not name such a file glsl.slang — that collides with Slang's built-in `glsl` module.)

layout(location = 0) in vec3 inPosition;

layout(set = 0, binding = 0) uniform ShadowCamera {
    mat4 lightViewProj;
    mat4 model;
} cam;

void main() {
    gl_Position = cam.lightViewProj * cam.model * vec4(inPosition, 1.0);
}
