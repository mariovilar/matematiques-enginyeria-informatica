#include "Lights/GPULight.hpp"

void GPULight::toGPU(GLuint p) {
    program = p;
}

void GPULight::updateToGPU(int index) {

    // Actualitzar les propietats de la llum a la GPU
    struct {
        GLuint Ia;
        GLuint Id;
        GLuint Is;
        GLuint enabled;
    } lightComponents;

    // Cal obtenir els identificadors de les variables uniform de la GPU
    // i actualitzar els seus valors amb les propietats de la llum
    string baseName = "lightComponents[" + to_string(index) + "].";
    lightComponents.Ia = glGetUniformLocation(program, (baseName + "Ia").c_str());
    lightComponents.Id = glGetUniformLocation(program, (baseName + "Id").c_str());
    lightComponents.Is = glGetUniformLocation(program, (baseName + "Is").c_str());
    lightComponents.enabled = glGetUniformLocation(program, (baseName + "enabled").c_str());

    // Comprovem si les localitzacions són vàlides
    if (lightComponents.Ia == -1) {
        // cerr << "Error: No s'ha pogut obtenir la localització de 'Ia'. Index " << index << endl;
        return;
    }
    if (lightComponents.Id == -1) {
        // cerr << "Error: No s'ha pogut obtenir la localització de 'Id'. Index " << index << endl;
        return;
    }
    if (lightComponents.Is == -1) {
        // cerr << "Error: No s'ha pogut obtenir la localització de 'Is'. Index " << index << endl;
        return;
    }
    if (lightComponents.enabled == -1) {
        // cerr << "Error: No s'ha pogut obtenir la localització de la variable 'enabled'. Index " << index << endl;
        return;
    }
    // Enviem les propietats de la llum a la GPU
    glUniform3fv(lightComponents.Ia, 1, glm::value_ptr(this->Ia));
    glUniform3fv(lightComponents.Id, 1, glm::value_ptr(this->Id));
    glUniform3fv(lightComponents.Is, 1, glm::value_ptr(this->Is));
    glUniform1i(lightComponents.enabled, this->enabled);
}

/**
 * NOTA IMPORTANT:
 * Traiem els "xivatos" que ens indiquen que no hem pogut obtenir la localització.
 * Això és degut a què depenent del shader no necessitem les llums, o els materials, o la llum global.
 * El propi compilador d'OpenGL s'encarrega de treure les variables que no fem servir.
 * Pel que si el GLSL no les fa servir dins el main, optimitzarà el programa obviant aquestes variables i 
 * serem incapaços d'obtenir la seva localització.
 */
