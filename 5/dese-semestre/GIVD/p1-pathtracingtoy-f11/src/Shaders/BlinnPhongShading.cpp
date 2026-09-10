#include "Shaders/BlinnPhongShading.hpp"

// Funció per calcular la il·luminació amb un únic punt d'intersecció amb el raig
vec3 BlinnPhongShading::shading(ShadeInfo &info)
{
    // Color final acumulat
    vec3 finalColor = vec3(0.0f);
    vec3 shadowColor = computeColorShadow(info.p);

    // Si no hi ha llums, només es retorna la component difusa
    if (lights.empty())
        return info.mat->getDiffuse(info.uv);

    // Normal del punt d'intersecció
    vec3 N = normalize(info.normal);

    // Direcció de la càmera
    vec3 V = normalize(lookFrom - info.p);

    // Component ambient global
    vec3 colorAmbient = info.mat->Ka * this->scene->lightGlobal;

    for (const auto &lightObject : lights)
    {
        // Fem casting a PointLight per utilitzar a, b, c
        PointLight *light = static_cast<PointLight *>(lightObject.get());

        // Direcció de la llum
        vec3 L = normalize(light->vectorL(info.p));

        // Vector mitjà
        vec3 H = normalize(L + V);

        // Distància a la llum
        float d = length(light->vectorL(info.p));

        // Guardem l'atenuació i fem la divisió en cas que sigui possible
        float atenuacio = 1.0f;
        float denominator = light->getA() + light->getB() * d + light->getC() * d * d;
        if (denominator > 0.0f)
            atenuacio = 1.0f / denominator;

        // Angle entre la normal i la llum
        float cosLN = glm::max(dot(L, N), 0.0f);

        // Angle entre la normal i el vector mitjà
        float cosNH = glm::max(dot(N, H), 0.0f);

        // Component especular
        float beta = info.mat->shininess;
        vec3 colorEspecular = info.mat->Ks * light->getIs() * pow(cosNH, beta);

        // Component difusa
        vec3 diffuse = info.mat->getDiffuse(info.uv);
        vec3 colorDifus = diffuse * light->getId() * cosLN;

        // Contribució de la llum amb atenuació
        finalColor += atenuacio * (colorDifus + colorEspecular);
    }

    // Multipliquem pel factor ombra si està definit
    if (shadow)
        finalColor *= shadowColor;

    // Afegim la component ambient
    finalColor += colorAmbient;

    // Limitar la sortida a 1.0
    finalColor = glm::clamp(finalColor, vec3(0.0f), vec3(1.0f));

    return finalColor;
}

// Funció preparada per manegar més d'una intersecció
vec3 BlinnPhongShading::shading(vector<shared_ptr<ShadeInfo>> infos)
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