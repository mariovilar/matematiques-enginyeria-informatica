#include "GPUMaterial.hpp"
#include <iostream>

GPUMaterial::GPUMaterial(): Ka(0.2f), Kd(1.0f), Ks(0.8f) {
    shininess = 100.0f;
    opacity = 1;
};
GPUMaterial::GPUMaterial(vec3 d):  Ka(0.2f), Kd(d), Ks(0.8f) {
    shininess = 100.0f;
    opacity = 1;
};

GPUMaterial::GPUMaterial(vec3 a, vec3 d, vec3 s, float shininess):  Ka(a), Kd(d), Ks(s), shininess(shininess) {
    opacity = 1;
};

void GPUMaterial::toGPU(GLuint program) {
    // Enviar les propietats del material a la GPU
    this->program = program;
    struct {
        GLuint Ka;
        GLuint Kd;
        GLuint Ks;
        GLuint shininess;
        GLuint opacity;
    } material;
    material.Ka = glGetUniformLocation(program, "materialComponents.Ka");
    material.Kd = glGetUniformLocation(program, "materialComponents.Kd");
    material.Ks = glGetUniformLocation(program, "materialComponents.Ks");
    material.shininess = glGetUniformLocation(program, "materialComponents.shininess");
    material.opacity = glGetUniformLocation(program, "materialComponents.opacity");

    if(material.Ka == -1) {
        // std::cerr << "Error: material.Ka not found in shader program." << std::endl;
        return;
    }
    if(material.Kd == -1) {
        // std::cerr << "Error: material.Kd not found in shader program." << std::endl;
        return;
    }
    if(material.Ks == -1) {
        // std::cerr << "Error: material.Ks not found in shader program." << std::endl;
        return;
    }
    if(material.shininess == -1) {
        // std::cerr << "Error: material.shininess not found in shader program." << std::endl;
        return;
    }
    if(material.opacity == -1) {
        // std::cerr << "Error: material.opacity not found in shader program." << std::endl;
        return;
    }
    glUniform3fv(material.Ka, 1, glm::value_ptr(this->Ka));
    glUniform3fv(material.Kd, 1, glm::value_ptr(this->Kd));
    glUniform3fv(material.Ks, 1, glm::value_ptr(this->Ks));
    glUniform1f(material.shininess, this->shininess);
    glUniform1f(material.opacity, this->opacity);
}

/**
 * P: Des d’on es cridarà GPUMaterial::toGPU()?
 * R: Normalment, es crida des del mètode draw() de la classe Object. 
 *    Just abans de cridar glDrawArrays o glDrawElements per dibuixar la geometria de l'objecte, 
 *    cal assegurar-se que el shader program correcte està actiu (program->use()) i llavors cridar 
 *    material->toGPU(program->getId()) per configurar els uniforms del material per a aquest objecte específic.
 */

 /**
 * NOTA IMPORTANT:
 * Traiem els "xivatos" que ens indiquen que no hem pogut obtenir la localització.
 * Això és degut a què depenent del shader no necessitem les llums, o els materials, o la llum global.
 * El propi compilador d'OpenGL s'encarrega de treure les variables que no fem servir.
 * Pel que si el GLSL no les fa servir dins el main, optimitzarà el programa obviant aquestes variables i 
 * serem incapaços d'obtenir la seva localització.
 */