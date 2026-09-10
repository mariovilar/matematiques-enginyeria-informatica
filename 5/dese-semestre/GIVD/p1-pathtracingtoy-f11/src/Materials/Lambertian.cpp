#include "Materials/Lambertian.hpp"

Lambertian::Lambertian(const vec3 &color) : Material(color)
{
    Kd = color;
}

Lambertian::Lambertian(const vec3 &a, const vec3 &d, const vec3 &s, const float k) : Material(a, d, s, k) {}

Lambertian::Lambertian(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o) : Material(a, d, s, k, o) {}

Lambertian::~Lambertian() {}

bool Lambertian::evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const
{
    vec3 rec = r_in(t);
    vec3 target = rec + normal + glm::ballRand(1.0f);
    r_out = Ray(rec, target - rec);
    color = Kd;
    return false;
}

vec3 Lambertian::getDiffuse(vec2 uv) const
{
    return Kd;
}
