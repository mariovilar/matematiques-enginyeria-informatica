#pragma once

#include "Materials/Material.hpp"
#include <glm/glm.hpp>
#include <glm/gtc/random.hpp> // Per a glm::ballRand

class Transparent : public Material
{
public:
    Transparent() {};
    Transparent(const vec3 &color);
    Transparent(const vec3 &a, const vec3 &d, const vec3 &s, const float k);
    Transparent(const vec3 &a, const vec3 &d, const vec3 &s, const float k, const float o);
    virtual ~Transparent();
    void setNu(float nu);
    float getNu() { return nut; };
    void setDmax(float dmax);
    float getDmax() { return dmax; };
    virtual bool evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const override;
    virtual vec3 getDiffuse(vec2 point) const override;
private:
    float reflectionRatio(vec3 I, vec3 &normal) const;
    float nut = 1.0f;
    float dmax = 1.0f;
};
