#include "Materials/Transparent.hpp"
#include <iostream>

Transparent::Transparent(const vec3 &color) : Material(color)
{
    Kd = color;
}

Transparent::Transparent(const vec3 &a, const vec3 &d, const vec3 &s, const float k) : Material(a, d, s, k) {}

Transparent::Transparent(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o) : Material(a, d, s, k, o) {}

Transparent::~Transparent() {}

void Transparent::setNu(float nu) {
    this->nut = nu;
}

void Transparent::setDmax(float dmax) {
    this->dmax = dmax;
}

float Transparent::reflectionRatio(vec3 I, vec3& normal) const {
    float reflection_ratio;
    if (dot(I, normal) > 0) {
        // Ray exiting transparent material
        normal = -normal;
        reflection_ratio = nut;
    }
    else {
        // Ray entering transparent material
        reflection_ratio = 1.f / nut;
    }
    return reflection_ratio;
}

bool Transparent::evaluate(const Ray &r_in, float t, vec3 n, vec3 &color, Ray &r_out) const
{
    vec3 I = glm::normalize(r_in.direction);
    vec3 normal = glm::normalize(n);
    vec3 rec = r_in(t) + FLT_EPSILON * I;

    float ratio = reflectionRatio(I, normal);
    vec3 refracted = glm::refract(I, normal, ratio);

    if (length(refracted) < FLT_EPSILON) {
        // Total internal reflection
        vec3 reflected = glm::reflect(I, normal);
        r_out = Ray(rec, reflected);
        color = Ks; // Reflected color
    } else {
        // Ray is transmitted
        r_out = Ray(rec, refracted);
        color = Kt; // Transmitted color
    }
    return true;
}

vec3 Transparent::getDiffuse(vec2 uv) const
{
    return Kd;
}
