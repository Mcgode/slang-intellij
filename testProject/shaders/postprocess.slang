// Tonemapping pass drawn as a single fullscreen triangle.

Texture2D    gHdrColor;
SamplerState gLinearClamp;

struct FullscreenVertex
{
    float4 position : SV_Position;
    float2 uv;
}

static const float2 kFullscreenTriangle[3] = {
    float2(-1.0, -1.0),
    float2(-1.0,  3.0),
    float2( 3.0, -1.0),
};

[shader("vertex")]
FullscreenVertex vertexMain(uint vertexId : SV_VertexID)
{
    FullscreenVertex v;
    float2 p = kFullscreenTriangle[vertexId];
    v.position = float4(p, 0.0, 1.0);
    v.uv = p * 0.5 + 0.5;
    return v;
}

float3 reinhard(float3 hdr)
{
    return hdr / (hdr + 1.0);
}

[shader("fragment")]
float4 fragmentMain(FullscreenVertex input) : SV_Target
{
    float3 hdr = gHdrColor.Sample(gLinearClamp, input.uv).rgb;
    float3 mapped = reinhard(hdr);
    float3 gamma = pow(mapped, float3(1.0 / 2.2));
    return float4(gamma, 1.0);
}
