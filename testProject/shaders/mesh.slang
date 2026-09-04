#include "common/brdf.slangh"
import common.lighting;

struct VSInput
{
    float3 position : POSITION;
    float3 normal   : NORMAL;
    float2 uv       : TEXCOORD0;
}

struct VSOutput
{
    float4 clipPos : SV_Position;
    float3 worldNormal;
    float3 worldPos;
    float2 uv;
}

struct FrameConstants
{
    float4x4 viewProjection;
    float4x4 model;
    float3   cameraPosition;
    float    time;
}

ConstantBuffer<FrameConstants> gFrame;
uniform DirectionalLight gSun;

Texture2D    gAlbedo;
SamplerState gSampler;

[shader("vertex")]
VSOutput vertexMain(VSInput input)
{
    VSOutput output;

    float4 world = mul(gFrame.model, float4(input.position, 1.0));
    output.worldPos = world.xyz;
    output.clipPos = mul(gFrame.viewProjection, world);
    output.worldNormal = mul((float3x3)gFrame.model, input.normal);
    output.uv = input.uv;

    return output;
}

[shader("fragment")]
float4 fragmentMain(VSOutput input) : SV_Target
{
    float3 baseColor = gAlbedo.Sample(gSampler, input.uv).rgb;
    float3 viewDir = normalize(gFrame.cameraPosition - input.worldPos);

    Lambert diffuse;
    diffuse.albedo = baseColor;

    float3 lit = applyLighting(diffuse, input.worldNormal, viewDir, gSun);
    float3 ambient = baseColor * INV_PI * 0.1;

    return float4(lit + ambient, 1.0);
}
