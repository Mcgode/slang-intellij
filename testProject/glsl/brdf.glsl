// Shared BRDF helpers, #included by the lighting shaders.
// A .glsl file with no #version: the extension alone puts slangd in GLSL mode, so this has full
// language-server support on its own, and #include drops it into the including shader unchanged.
#ifndef BRDF_GLSL
#define BRDF_GLSL

const float PI = 3.14159265359;

float distributionGGX(vec3 n, vec3 h, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float ndh = max(dot(n, h), 0.0);
    float denom = ndh * ndh * (a2 - 1.0) + 1.0;
    return a2 / (PI * denom * denom);
}

float geometrySchlickGGX(float ndv, float roughness) {
    float r = roughness + 1.0;
    float k = (r * r) / 8.0;
    return ndv / (ndv * (1.0 - k) + k);
}

float geometrySmith(vec3 n, vec3 v, vec3 l, float roughness) {
    return geometrySchlickGGX(max(dot(n, v), 0.0), roughness)
         * geometrySchlickGGX(max(dot(n, l), 0.0), roughness);
}

vec3 fresnelSchlick(float cosTheta, vec3 f0) {
    return f0 + (vec3(1.0) - f0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

#endif
