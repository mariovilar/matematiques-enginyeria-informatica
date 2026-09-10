#pragma once

#include "Materials/Material.hpp"
#include <glm/glm.hpp>
#include <glm/gtc/random.hpp> // Per a glm::ballRand

class Metal : public Material
{
public:
    Metal() {};
    Metal(const vec3 &color);
    Metal(const vec3 &a, const vec3 &d, const vec3 &s, const float k);
    Metal(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o);
    virtual ~Metal();

    virtual bool evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const override;
    virtual vec3 getDiffuse(vec2 point) const override;
};
