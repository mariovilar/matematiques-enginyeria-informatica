#include "Shaders/ToonShading.hpp"

// Funció per calcular la il·luminació amb un únic punt d'intersecció amb el raig
vec3 ToonShading::shading(ShadeInfo &info) {
    // Si no hi ha cap llum, retornem el color difús del material
    if (lights.empty()) {
        return info.mat->getDiffuse(info.uv);
    }
    float shadowFactor = computeShadow(info.p);
    vec3 colorAmbient = info.mat->Ka * lights[0]->getIa();
    
    // Obtenim la direcció de la llum des del punt d'intersecció
    // La funció vectorL de la llum ens proporciona el vector des del punt a la font de llum.
    vec3 lightDir = normalize(lights[0]->vectorL(info.p));
    
    // Normalitzem la normal del punt d'intersecció
    vec3 normal = normalize(info.normal);
    
    // Calculem el cosinus de l'angle entre la normal i la direcció de la llum
    float cosTheta = glm::max(dot(normal, lightDir), 0.0f);
    
    // En cas contrari, calcula el color corresponent a la il·luminació difusa
    vec3 diff = info.mat->getDiffuse(info.uv);
    vec3 diffuseColor = diff * lights[0]->getId();

    // Retornem el color quantitzant el valor del cosinus de l'angle
    float levels = 4.0f; // Number of quantization levels
    cosTheta = floor(cosTheta * levels) / levels;
    // Apply the quantized cosTheta to the diffuse color
    diffuseColor *= cosTheta;

    // Apliquem el factor d'ombra i la il·luminació ambient
    vec3 finalColor = (1.0f - shadowFactor) * diffuseColor + colorAmbient;
    return finalColor;
}

// Copiada del DiffuseShading, però cal modificar-la perquè retorni el color en funció de la normal
vec3 ToonShading::shading(vector<shared_ptr<ShadeInfo>> infos) {
    if (!infos.empty()) {
        return infos[0]->mat->Kd; // TODO: A modificar per retornar el color en funció de la normal en el punt d'intersecció
    } else {
        // Tracta la situació on el vector infos és buit
        // Retorna un valor predeterminat o maneja-ho segons les teves necessitats.
        return vec3(0.0f);
    }
}