#version 450

layout(location = 0) in vec3 vNormal;
layout(location = 0) out vec4 outColor;

layout(push_constant) uniform Push {
    vec3 lightDir;
} push;

void main() {
    float ndl = max(dot(normalize(vNormal), -normalize(push.lightDir)), 0.0);
    outColor = vec4(vec3(ndl), 1.0);
}
