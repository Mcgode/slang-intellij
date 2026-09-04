#version 450

layout(location = 0) in vec3 fragNormal;
layout(location = 1) in vec2 fragTexCoord;
layout(location = 2) in vec3 fragWorldPos;

layout(location = 0) out vec4 outColor;

layout(set = 0, binding = 1) uniform sampler2D albedoTex;

struct Light {
    vec3 position;
    vec3 color;
    float radius;
};

layout(std140, set = 0, binding = 2) uniform LightBuffer {
    Light lights[16];
    int count;
} lightData;

// Point-light contribution with a linear distance falloff.
vec3 shade(Light light, vec3 normal, vec3 worldPos, vec3 albedo) {
    vec3 toLight = light.position - worldPos;
    float dist = length(toLight);
    float attenuation = clamp(1.0 - dist / light.radius, 0.0, 1.0);
    float ndl = max(dot(normal, normalize(toLight)), 0.0);
    return albedo * light.color * (ndl * attenuation);
}

void main() {
    vec3 normal = normalize(fragNormal);
    vec3 albedo = texture(albedoTex, fragTexCoord).rgb;

    vec3 color = albedo * 0.03;
    for (int i = 0; i < lightData.count; ++i) {
        color += shade(lightData.lights[i], normal, fragWorldPos, albedo);
    }

    outColor = vec4(color, 1.0);
}
