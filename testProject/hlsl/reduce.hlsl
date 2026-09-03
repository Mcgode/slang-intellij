// Compute shader: parallel sum reduction over a buffer.

StructuredBuffer<float>   gInput  : register(t0);
RWStructuredBuffer<float>  gOutput : register(u0);

cbuffer Params : register(b0)
{
    uint gCount;
};

groupshared float gScratch[256];

[numthreads(256, 1, 1)]
void main(uint3 dtid : SV_DispatchThreadID, uint gi : SV_GroupIndex, uint3 gid : SV_GroupID)
{
    uint index = dtid.x;
    gScratch[gi] = (index < gCount) ? gInput[index] : 0.0;
    GroupMemoryBarrierWithGroupSync();

    for (uint stride = 128; stride > 0; stride >>= 1)
    {
        if (gi < stride)
        {
            gScratch[gi] += gScratch[gi + stride];
        }
        GroupMemoryBarrierWithGroupSync();
    }

    if (gi == 0)
    {
        gOutput[gid.x] = gScratch[0];
    }
}
