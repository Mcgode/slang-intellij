#ifndef BRDF_SLANGH
#define BRDF_SLANGH

// Object-like macros
#define PI          3.14159265358979
#define INV_PI      (1.0 / PI)
#define MAX_LIGHTS  4

// Function-like macro
#define SATURATE(x) clamp((x), 0.0, 1.0)

// Multi-line function-like macro (line continuations)
#define LUMINANCE(rgb) ( 0.2126 * (rgb).r \
                       + 0.7152 * (rgb).g \
                       + 0.0722 * (rgb).b )

float3 fresnelSchlick(float cosTheta, float3 f0)
{
    float m = SATURATE(1.0 - cosTheta);
    return f0 + (1.0 - f0) * (m * m * m * m * m);
}

#endif // BRDF_SLANGH
