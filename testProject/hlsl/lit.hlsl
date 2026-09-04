#include "common.hlsli"

StructuredBuffer<Light> gLights : register(t0);

cbuffer LightParams : register(b0)
{
    uint gLightCount;
};

Texture2D    gAlbedo : register(t1);
SamplerState gSamp   : register(s0);

struct PSInput
{
    float4 clipPos  : SV_Position;
    float3 worldPos : TEXCOORD0;
    float3 normal   : TEXCOORD1;
    float2 uv       : TEXCOORD2;
};

float4 main(PSInput input) : SV_Target
{
    float3 albedo = gAlbedo.Sample(gSamp, input.uv).rgb;
    float3 n = normalize(input.normal);

    float3 color = albedo * 0.03;
    for (uint i = 0; i < gLightCount; ++i)
    {
        color += diffuse(gLights[i], n, input.worldPos, albedo);
    }

    return float4(color / PI, 1.0);
}
