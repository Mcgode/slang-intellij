#version 450

// Intentionally broken: diagnostics should show squiggles and Problems entries.

layout(location = 0) in vec2 fragTexCoord;
layout(location = 0) out vec4 outColor;

void main() {
    vec3 color = texture(missingSampler, fragTexCoord).rgb;  // undeclared 'missingSampler'
    float lum = color;                                       // vec3 assigned to float
    outColor = vec4(lum);
}
