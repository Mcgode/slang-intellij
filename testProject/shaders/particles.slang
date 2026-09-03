// GPU particle simulation — compute entry point.

struct Particle
{
    float3 position;
    float3 velocity;
    float  life;
}

struct SimParams
{
    float3 gravity;
    float  deltaTime;
    uint   particleCount;
    uint   p;
}

ConstantBuffer<SimParams>    gParams;
RWStructuredBuffer<Particle>  gParticles;

static const float kResetLifetime = 5.0;

[shader("compute")]
[numthreads(64, 1, 1)]
void simulate(uint3 threadId : SV_DispatchThreadID)
{
    let index = threadId.x;
    if (index >= gParams.particleCount)
        return;

    Particle p = gParticles[index];

    p.velocity += gParams.gravity * gParams.deltaTime;
    p.position += p.velocity * gParams.deltaTime;
    p.life -= gParams.deltaTime;

    if (p.life <= 0.0)
    {
        p.position = float3(0.0);
        p.velocity = float3(0.0);
        p.life = kResetLifetime;
    }

    gParticles[index] = p;
}
