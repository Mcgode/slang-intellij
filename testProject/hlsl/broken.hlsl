// Intentionally broken: diagnostics should show squiggles and Problems entries.

Texture2D    gTex  : register(t0);
SamplerState gSamp : register(s0);

float4 main(float2 uv : TEXCOORD0) : SV_Target
{
    float3 color = gTex.Sample(gSampler, uv).rgb;   // wrong name: gSampler vs gSamp
    float  x = color;                               // float3 -> float
    return float4(color, 1.0);
}
