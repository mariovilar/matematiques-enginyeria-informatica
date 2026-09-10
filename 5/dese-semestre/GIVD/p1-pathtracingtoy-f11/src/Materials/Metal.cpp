#include "Materials/Metal.hpp"

Metal::Metal(const vec3 &color) : Material(color)
{
    Kd = color;
}

Metal::Metal(const vec3 &a, const vec3 &d, const vec3 &s, const float k) : Material(a, d, s, k) {}

Metal::Metal(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o) : Material(a, d, s, k, o) {}

Metal::~Metal() {}

bool Metal::evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const
{
    // Punt d'impacte
    vec3 I = normalize(r_in.direction);
    vec3 rec = r_in(t) + FLT_EPSILON * I;

    // Reflexió especular
    vec3 reflected = glm::reflect(I, normal);

    // Rugositat de la superfície si n'hi ha
    if (roughness > 0.0f) {
        reflected += roughness * glm::ballRand(1.0f);
    }

    // Raig reflectit
    r_out = Ray(rec, reflected);
    color = Ks;

    return true;
}

vec3 Metal::getDiffuse(vec2 uv) const
{
    return Kd;
}
