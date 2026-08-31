// This file intentionally contains errors. Opening it should produce editor
// squiggles and entries in the Problems view, driven by slangd diagnostics.
// Everything else in testProject/ is expected to be diagnostic-clean.

struct Material
{
    float3 albedo;
    float  roughness;
}

float4 brokenFunction(float3 n, Material mat)
{
    // undeclared identifier
    float3 a = normalize(undeclaredVariable);

    // wrong argument count for a builtin
    float d = dot(n);

    // type mismatch: float3 assigned to float
    float b = n;

    // unknown member
    float r = mat.metallic;

    // returning float3 where float4 is expected
    return a + b + r;
}
