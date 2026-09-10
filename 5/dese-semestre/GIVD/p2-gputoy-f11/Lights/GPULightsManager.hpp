#pragma once

#include <vector>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "GPULight.hpp"
#include "GPUPointLight.hpp"


class GPULightsManager  {
    private:
        // Vector de llums contingudes la mon
        vector<shared_ptr<GPUPointLight>> lights;
        // Llum ambient global:
        vec3 globalAmbientLight;

    public:
        GPULightsManager()  {
            globalAmbientLight = vec3(0.2f, 0.2f, 0.2f);
        };
        
        void setGlobalAmbientLight(vec3 a) {
            globalAmbientLight = a;
        };

        void toGPU(GLuint program) {

            if (program == 0) {
                return;
            }

            // Enviar llum ambient global a la GPU (cada vegada que s'envia l'estat complet de la il·luminació 
            // (com a la inicialització o després d'un updateAllLights), la llum ambient global també s'actualitza a la GPU)
            globalAmbientLightToGPU(program, this->globalAmbientLight);
            
            // Enviar tota la informació de la il·luminació a la GPU (llums actives)
            int activeLights = 0;
            for (int i = 0; i < lights.size(); i++) {
                
                // Enviem la llum a la GPU
                lights[i]->toGPU(program);

                if(lights[i]->isEnabled()) {
                    activeLights++;
                    // Posar en el shader el nombre de llums actives
                    numActiveLightsToGPU(program, activeLights);
                    // Actualitzem les propietats de la llum a la GPU
                    // (això és important perquè les llums poden canviar de posició, color, etc.
                    // i cal que la GPU tingui la informació més recent)
                    lights[i]->updateToGPU(i);
                }
            }
        };        

        void addLight(shared_ptr<GPUPointLight> l) {
            lights.push_back(l);
        };

        void numActiveLightsToGPU(GLuint program, int activeLights) {
            if (program == 0) {
                return;
            }
            GLuint activeLightsLoc = glGetUniformLocation(program, "numActiveLights");
            if (activeLightsLoc == -1) {
                // std::cerr << "Error: numActiveLights not found in shader program." << std::endl;
                return;
            }
            glUniform1i(activeLightsLoc, activeLights);
        };

        void globalAmbientLightToGPU(GLuint program, vec3 a) {
            this->globalAmbientLight = a;
            GLuint ambient = glGetUniformLocation(program, "globalAmbientLight");
            if (ambient == -1) {
                // std::cerr << "Error: globalAmbientLight not found in shader program." << std::endl;
                return;
            }
            glUniform3fv(ambient, 1, glm::value_ptr(this->globalAmbientLight));
        };

        // Es crida quan es modifica un paràmetre de *qualsevol llum* (posició, color, atenuació, etc.)
        // Això assegura que la GPU sempre tingui la informació més recent de totes les llums.
        void updateAllLights(GLuint program, vector<GPUPointLight> &lightsNew) {
            if (program == 0) 
                return; 
            
            lights.clear();

            for (int i = 0; i < lightsNew.size(); i++) {
                lights.push_back(make_shared<GPUPointLight>(
                    lightsNew[i].getPos(),
                    lightsNew[i].getIa(),
                    lightsNew[i].getId(),
                    lightsNew[i].getIs(),
                    lightsNew[i].getA(),
                    lightsNew[i].getB(),
                    lightsNew[i].getC(),
                    lightsNew[i].isEnabled()
                ));
            }
            
            // Enviem totes les llums a la GPU (exposem el per què a sota)
            toGPU(program);
        };
        /**
         * P: Cal enviar totes les llums a la GPU des d'aquest mètode?
         * R: Sí, perquè la funció updateAllLights té com a objectiu reemplaçar completament el conjunt de llums existent amb un nou conjunt
         *    L'estat de les llums a la CPU ha canviat totalment. Per tant, la informació que hi havia prèviament a la GPU 
         *    (l'array de llums i el comptador) ja no és vàlida i s'ha de sobreescriure completament amb les dades de les noves llums.
        */

        void updateSingleLight(GLuint program, GPUPointLight &pl, int index) {
            if (program == 0) return;
            
            lights[index] = make_shared<GPUPointLight>(
                pl.getPos(),
                pl.getIa(),
                pl.getId(),
                pl.getIs(),
                pl.getA(),
                pl.getB(),
                pl.getC(),
                pl.isEnabled()
            );
            // Actualitzar la llum a la GPU
            lights[index]->toGPU(program);
            lights[index]->updateToGPU(index);
        };

        /**
         * 1. Quan cridar GPULightsManager::toGPU(program)?
         * 1.1. Inicialització (initializeGL o similar): Un cop després d'haver compilat, enllaçat i activat (glUseProgram) el shader program per primer cop. 
         *      Això estableix l'estat inicial de la il·luminació a la GPU.
         * 1.2. Canvi de Shader: Canviant a un altre shader program que també utilitza aquest sistema de llums, cridar toGPU amb el nou ID del programa 
         *      després d'activar-lo (glUseProgram(newProgramID)). Les localitzacions de les uniforms són específiques de cada programa.
         * 1.3. Canvis Estructurals a les Llums: Si afegeixes o elimines llums del vector lights, o si canvies quines llums estan actives/inactives 
         *      (si implementes el flag isEnabled), has de cridar toGPU per actualitzar l'array complet i el comptador numActiveLights a la GPU. 
         *      La funció updateAllLights ja ho fa.
         * 2. Quan cridar GPULightsManager::updateSingleLight(program, index)? 
         *    Crida aquest mètode quan només canvien les propietats (posició, color, atenuació) d'una llum que ja existeix i que ja ha estat enviada 
         *    a la GPU prèviament, i el nombre total de llums actives no canvia. És més eficient que reenviar-ho tot amb toGPU. 
         * 3. Quan cridar GPULightsManager::globalAmbientLight(program)?
         *    Crida aquest mètode si només canvia la llum ambient global i la resta de llums puntuals no han canviat.
         */
};

/**
 * NOTA IMPORTANT:
 * Traiem els "xivatos" que ens indiquen que no hem pogut obtenir la localització.
 * Això és degut a què depenent del shader no necessitem les llums, o els materials, o la llum global.
 * El propi compilador d'OpenGL s'encarrega de treure les variables que no fem servir.
 * Pel que si el GLSL no les fa servir dins el main, optimitzarà el programa obviant aquestes variables i 
 * serem incapaços d'obtenir la seva localització.
 */