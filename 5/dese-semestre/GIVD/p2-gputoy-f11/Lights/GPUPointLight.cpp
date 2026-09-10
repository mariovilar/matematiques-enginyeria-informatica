#include "GPUPointLight.hpp"

GPUPointLight::GPUPointLight(): GPULight() {
    this->pos = vec3(0.0f, 0.4f, 0.4f);
}

GPUPointLight::GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is): GPULight(Ia, Id, Is) {
    this->pos = posicio;
    this->a = 0.0f;
    this->b = 0.0f;
    this->c = 0.0f;
}

GPUPointLight::GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c): GPULight(Ia, Id, Is) {
    this->pos = posicio;
    this->a = a;
    this->b = b;
    this->c = c;
}

GPUPointLight::GPUPointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c, bool isEnabled): GPULight(Ia, Id, Is, isEnabled) {
    this->pos = posicio;
    this->a = a;
    this->b = b;
    this->c = c;
}

vec3 GPUPointLight::vectorL(vec3 point) {
    return normalize(pos - point);
}

float GPUPointLight::attenuation(vec3 point) {
    if (abs(a)<DBL_EPSILON && abs(b)<DBL_EPSILON  && abs(c)<DBL_EPSILON) {
        //Si tots els coeficients son 0 considerem que no hi ha atenuacio
        return 1.0f;
    }
    //Calculem la distancia entre el punt i la posicio de la llum
    float d = distance(point, pos);
    return 1.0f/(c*d*d + b*d + a);
}

float GPUPointLight::distanceToLight(vec3 point) {
    return distance(point, pos);
}

void GPUPointLight::toGPU(GLuint pr) {
    GPULight::toGPU(pr);
}

void GPUPointLight::updateToGPU(int index) {

    // Executem el mètode updateToGPU de la classe base GPULight
    // per actualitzar les propietats bàsiques de la llum (Ia, Id, Is, enabled)
    GPULight::updateToGPU(index);

    // Actualitzar les propietats de la llum puntual a la GPU
    struct {
        GLuint pos;
        GLuint a;
        GLuint b;
        GLuint c;
    } lightComponents;

    // Cal obtenir els identificadors de les variables uniform de la GPU de la llum amb index "index"
    // i actualitzar els seus valors amb les propietats de la llum puntual
    string baseName = "lightComponents[" + to_string(index) + "].";
    lightComponents.pos = glGetUniformLocation(program, (baseName + "pos").c_str());
    lightComponents.a = glGetUniformLocation(program, (baseName + "a").c_str());
    lightComponents.b = glGetUniformLocation(program, (baseName + "b").c_str());
    lightComponents.c = glGetUniformLocation(program, (baseName + "c").c_str());

    // Comprovem si hem obtingut correctament les variables uniform
    if (lightComponents.pos == -1) {
        // std::cerr << "Error: No s'ha pogut obtenir la variable uniform 'pos' de la llum puntual. Index " << index << std::endl;
        return;
    }
    if (lightComponents.a == -1) {
        // std::cerr << "Error: No s'ha pogut obtenir la variable uniform 'a' de la llum puntual. Index" << index << std::endl;
        return;
    }
    if (lightComponents.b == -1) {
        // std::cerr << "Error: No s'ha pogut obtenir la variable uniform 'b' de la llum puntual. Index " << index << std::endl;
        return;
    }
    if (lightComponents.c == -1) {
        // std::cerr << "Error: No s'ha pogut obtenir la variable uniform 'c' de la llum puntual. Index " << index << std::endl;
        return;
    }

    // Enviem les propietats de la llum puntual a la GPU
    glUniform3fv(lightComponents.pos, 1, glm::value_ptr(this->pos));
    glUniform1f(lightComponents.a, this->a);
    glUniform1f(lightComponents.b, this->b);
    glUniform1f(lightComponents.c, this->c);
}

/**
 * NOTA IMPORTANT:
 * Traiem els "xivatos" que ens indiquen que no hem pogut obtenir la localització.
 * Això és degut a què depenent del shader no necessitem les llums, o els materials, o la llum global.
 * El propi compilador d'OpenGL s'encarrega de treure les variables que no fem servir.
 * Pel que si el GLSL no les fa servir dins el main, optimitzarà el programa obviant aquestes variables i 
 * serem incapaços d'obtenir la seva localització.
 */