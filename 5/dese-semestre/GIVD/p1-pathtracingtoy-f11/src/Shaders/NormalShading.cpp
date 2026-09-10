#include "Shaders/NormalShading.hpp"

// Funció per calcular la il·luminació amb un únic punt d'intersecció amb el raig
vec3 NormalShading::shading(ShadeInfo &info)
{
    // Comprovem si el punt d'intersecció està a l'ombra
    float shadowFactor = computeShadow(info.p);
    vec3 transparentShadow = computeColorShadow(info.p);
    vec3 ambient = info.mat->Ka * lights[0]->getIa();

    // Normalitzem la normal del punt d'intersecció
    vec3 n = normalize(info.normal);
    vec3 normalColor = 0.5f * (n + vec3(1.0f));
    if (shadow)
    {
        normalColor *= (1 - shadowFactor);
    }
    vec3 pixelColor = normalColor + ambient;
    return glm::clamp(pixelColor, vec3(0.0f), vec3(1.0f));
}

// Funció preparada per manegar més d'una intersecció
vec3 NormalShading::shading(vector<shared_ptr<ShadeInfo>> infos)
{
    if (!infos.empty())
    {
        return infos[0]->mat->Kd; // TODO: A modificar per retornar el color en funció de la normal en el punt d'intersecció
    }
    else
    {
        // Tracta la situació on el vector infos és buit
        // Retorna un valor predeterminat o maneja-ho segons les teves necessitats.
        return vec3(0.0f);
    }
}
