#pragma once

#include <glm/glm.hpp>
#include <memory>
#include <random>

#include "Utilities/Ray.hpp"

using namespace glm;

// Classe abstracte Material.
// Totes les seves filles hauran de definir el metode abstracte "evaluate" implementat
class Material
{
public:
    Material() : Ka(1.0f), Kd(1.0f), Ks(1.0f)
    {
        shininess = 100.0f;
        setOpacity(1.f);
    };
    Material(vec3 d) : Ka(1.0f), Kd(d), Ks(1.0f)
    {
        shininess = 100.0f;
        albedo = d;
        setOpacity(1.f);
    };

    Material(vec3 a, vec3 d, vec3 s, float shininess) : Ka(a), Kd(d), Ks(s), albedo(d), shininess(shininess) {
        setOpacity(1.f);
    };

    Material(vec3 a, vec3 d, vec3 s, float shininess, float opacity) : Ka(a), Kd(d), Ks(s), albedo(d), shininess(shininess), opacity(opacity) {
        setOpacity(opacity);
    };
    ~Material() {};

    virtual bool evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const = 0;
    virtual vec3 getDiffuse(vec2 point) const { return Kd; };
    virtual void setKa(vec3 Ka) { this->Ka = Ka; };
    virtual void setKd(vec3 Kd) { this->Kd = Kd; };
    virtual void setKs(vec3 Ks) { this->Ks = Ks; };
    virtual void setKt(vec3 Kt) { this->Kt = Kt; };
    virtual void setAlbedo(vec3 albedo) { this->albedo = albedo; };
    virtual void setRoughness(float roughness) { this->roughness = roughness; };
    virtual void setOpacity(float opacity) 
    {
        this->opacity = opacity;
        vec3 Kt = vec3(1.f - opacity);
        setKt(Kt); 
    };

    virtual vec3 getColor(vec2 point) const { return albedo; };
    virtual float getRoughness(vec2 point) const { return roughness; };

    vec3 Ka;
    vec3 Kd;
    vec3 Ks;
    vec3 Kt;
    // Normalment 1-Ks, però totes les boles del test1() estan a Ks(1,1,1), 
    // pel que no tindríem transparència si no ho canviem més endavant

    vec3 albedo;
    float roughness;

    float shininess = 100.0f;
    float opacity = 1.f; // opacity es la fraccio de 0..1 (0 és totalment transparent, 1 és totalment opac)
};
