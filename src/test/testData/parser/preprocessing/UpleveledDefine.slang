#define POS m_position: POSITION0;

struct IaToVs
{
    [[vk::location(0)]] float3 POS
    [[vk::location(1)]] float4 m_color: COLOR0;
};