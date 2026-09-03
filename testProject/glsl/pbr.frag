#version 450
#extension GL_GOOGLE_include_directive : require

#include "brdf.glslh"

layout(location = 0) in vec3 fragNormal;
layout(location = 1) in vec3 fragWorldPos;
layout(location = 2) in vec3 fragViewPos;

layout(location = 0) out vec4 outColor;

layout(push_constant) uniform Material {
    vec3 albedo;
    float roughness;
    float metallic;
    vec3 lightPos;
    vec3 lightColor;
} mat;

void main() {
    vec3 n = normalize(fragNormal);
    vec3 v = normalize(fragViewPos - fragWorldPos);
    vec3 l = normalize(mat.lightPos - fragWorldPos);
    vec3 h = normalize(v + l);

    vec3 f0 = mix(vec3(0.04), mat.albedo, mat.metallic);

    float ndf = distributionGGX(n, h, mat.roughness);
    float g = geometrySmith(n, v, l, mat.roughness);
    vec3 f = fresnelSchlick(max(dot(h, v), 0.0), f0);

    vec3 numerator = ndf * g * f;
    float denom = 4.0 * max(dot(n, v), 0.0) * max(dot(n, l), 0.0) + 0.0001;
    vec3 specular = numerator / denom;

    vec3 kd = (vec3(1.0) - f) * (1.0 - mat.metallic);
    float ndl = max(dot(n, l), 0.0);

    vec3 color = (kd * mat.albedo / PI + specular) * mat.lightColor * ndl;
    outColor = vec4(color, 1.0);
}
