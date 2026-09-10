#pragma once

#include "Lights/Light.hpp"

/*
 * Classe PointLight que hereta de Light.
 * Aquesta classe representa una llum puntual.
 * */
class PointLight : public Light
{
public:
    PointLight() {};
    /*
     * Constructor de la classe PointLight.
     * param posicio: posició de la llum.
     * param Ia: component ambient de la llum.
     * param Id: component difosa de la llum.
     * param Is: component especular de la llum.
     * */
    PointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is);

    /*
     * Constructor de la classe PointLight.
     * param posicio: posició de la llum.
     * param Ia: component ambient de la llum.
     * param Id: component difosa de la llum.
     * param Is: component especular de la llum.
     * param a: coeficient a de l'atenuacio.
     * param b: coeficient b de l'atenuacio.
     * param c: coeficient c de l'atenuacio.
     * */
    PointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c);
    virtual ~PointLight() {}
    vec3 getPos() { return pos; };
    void setPos(vec3 pos) { this->pos = pos; };
    virtual vec3 vectorL(vec3 point) override;
    virtual float attenuation(vec3 point) override;
    virtual float distanceToLight(vec3 point) override;

    float getA() { return a; };
    float getB() { return b; };
    float getC() { return c; };

private:
    vec3 pos;
    float a; // atenuacio: terme constant
    float b; // atenuacio: terme lineal
    float c; // atenuacio: terme quadratic
};
