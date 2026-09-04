// A classic HLSL vertex + pixel pair in one file.

cbuffer PerFrame : register(b0)
{
    float4x4 gViewProj;
    float3   gCameraPos;
};

cbuffer PerObject : register(b1)
{
    float4x4 gModel;
};

Texture2D    gAlbedo   : register(t0);
SamplerState gLinear   : register(s0);

struct VSInput
{
    float3 position : POSITION;
    float3 normal   : NORMAL;
    float2 uv       : TEXCOORD0;
};

struct VSOutput
{
    float4 clipPos  : SV_Position;
    float3 worldPos : TEXCOORD0;
    float3 normal   : TEXCOORD1;
    float2 uv       : TEXCOORD2;
};

VSOutput VSMain(VSInput input)
{
    VSOutput o;
    float4 world = mul(gModel, float4(input.position, 1.0));
    o.worldPos = world.xyz;
    o.normal   = mul((float3x3)gModel, input.normal);
    o.uv       = input.uv;
    o.clipPos  = mul(gViewProj, world);
    return o;
}

float4 PSMain(VSOutput input) : SV_Target
{
    float3 albedo = gAlbedo.Sample(gLinear, input.uv).rgb;
    float3 n = normalize(input.normal);
    float3 v = normalize(gCameraPos - input.worldPos);
    float  rim = pow(1.0 - saturate(dot(n, v)), 2.0);
    return float4(albedo * (0.2 + rim), 1.0);
}
