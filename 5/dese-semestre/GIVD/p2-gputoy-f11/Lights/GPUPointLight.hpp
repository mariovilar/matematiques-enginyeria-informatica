#pragma once

#include "GPULight.hpp"

/*
 * Classe GPUPointLight que hereta de GPULight.
 * Aquesta classe representa una llum puntual.
 * */
class GPUPointLight: public GPULight {
public:
    GPUPointLight();
    /*
     * Constructor de la classe GPUPointLight.
     * param posicio: posició de la llum.
     * param Ia: component ambient de la llum.
     * param Id: component difosa de la llum.
     * param Is: component especular de la llum.
     * */
    GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is);

    /*
     * Constructor de la classe GPUPointLight.
     * param posicio: posició de la llum.
     * param Ia: component ambient de la llum.
     * param Id: component difosa de la llum.
     * param Is: component especular de la llum.
     * param a: coeficient a de l'atenuacio.
     * param b: coeficient b de l'atenuacio.
     * param c: coeficient c de l'atenuacio.
     * */
    GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c);
    GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c, bool isEnabled);
    virtual ~GPUPointLight() {}
    vec3     getPos() { return pos; };
    void     setPos(vec3 pos) { this->pos = pos; };
    float    getA() { return a; };
    // void     setA(float a) { this->a = a; };
    float    getB() { return b; };
    // void     setB(float b) { this->b = b; };
    float    getC() { return c; };
    // void     setC(float c) { this->c = c; };
    //Calcula el vector L amb origen el punt passat per parametre
    virtual vec3 vectorL(vec3 point) override;
    virtual float attenuation(vec3 point) override;
    virtual float distanceToLight(vec3 point) override;

    // GPUConnectable interface implementation
    virtual void toGPU(GLuint pr) override;
    // Light overriding
    virtual void updateToGPU(int index) override; 
    
private:
    vec3 pos;
    float a; // atenuacio: terme constant
    float b; // atenuacio: terme lineal
    float c; // atenuacio: terme quadratic
};

