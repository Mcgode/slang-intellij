// Slang module, imported by the shaders under ../shaders.
// slangd resolves this through the workspace (searchInAllWorkspaceDirectories).
module lighting;

public struct DirectionalLight
{
    public float3 direction;
    public float3 color;
    public float  intensity;
}

public interface ILightModel
{
    float3 shade(float3 normal, float3 viewDir, DirectionalLight light);
}

public struct Lambert : ILightModel
{
    public float3 albedo;

    public float3 shade(float3 normal, float3 viewDir, DirectionalLight light)
    {
        float ndotl = max(0.0, dot(normalize(normal), normalize(-light.direction)));
        return albedo * light.color * light.intensity * ndotl;
    }
}

public struct BlinnPhong : ILightModel
{
    public float3 specularColor;
    public float  shininess;

    public float3 shade(float3 normal, float3 viewDir, DirectionalLight light)
    {
        float3 n = normalize(normal);
        float3 l = normalize(-light.direction);
        float3 h = normalize(l + normalize(viewDir));
        float spec = pow(max(0.0, dot(n, h)), shininess);
        return specularColor * light.color * spec;
    }
}

public float3 applyLighting<T : ILightModel>(T model, float3 n, float3 v, DirectionalLight light)
{
    return model.shade(n, v, light);
}
