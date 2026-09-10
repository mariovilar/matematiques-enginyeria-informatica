#pragma once

#include "Materials/Material.hpp"
#include <glm/glm.hpp>
#include <glm/gtc/random.hpp> // Per a glm::ballRand

class Lambertian : public Material
{
public:
    Lambertian() {};
    Lambertian(const vec3 &color);
    Lambertian(const vec3 &a, const vec3 &d, const vec3 &s, const float k);
    Lambertian(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o);
    virtual ~Lambertian();

    virtual bool evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const override;
    virtual vec3 getDiffuse(vec2 point) const override;
};
