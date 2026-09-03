#version 450

layout(location = 0) in vec2 uv;
layout(location = 0) out vec4 outColor;

layout(set = 0, binding = 0) uniform sampler2D hdrColor;

layout(constant_id = 0) const int TONEMAP = 1;

const float GAMMA = 2.2;

vec3 reinhard(vec3 c) {
    return c / (c + vec3(1.0));
}

vec3 aces(vec3 x) {
    const float a = 2.51;
    const float b = 0.03;
    const float c = 2.43;
    const float d = 0.59;
    const float e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

void main() {
    vec3 hdr = texture(hdrColor, uv).rgb;

    vec3 mapped;
    switch (TONEMAP) {
        case 0:  mapped = hdr; break;
        case 1:  mapped = reinhard(hdr); break;
        default: mapped = aces(hdr); break;
    }

    outColor = vec4(pow(mapped, vec3(1.0 / GAMMA)), 1.0);
}
