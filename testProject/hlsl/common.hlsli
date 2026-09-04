// Shared helpers, #included by the lighting shaders.
#ifndef COMMON_HLSLI
#define COMMON_HLSLI

static const float PI = 3.14159265;

struct Light
{
    float3 position;
    float3 color;
    float  radius;
};

float3 diffuse(Light light, float3 normal, float3 worldPos, float3 albedo)
{
    float3 toLight = light.position - worldPos;
    float  dist = length(toLight);
    float  atten = saturate(1.0 - dist / light.radius);
    float  ndl = saturate(dot(normal, normalize(toLight)));
    return albedo * light.color * (ndl * atten);
}

#endif
