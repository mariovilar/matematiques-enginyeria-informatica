#include "Shaders/ShadingStrategy.hpp"
#include "Materials/Transparent.hpp"
#include "Materials/MaterialFactory.hpp"

float ShadingStrategy::computeShadow(vec3 point)
{
    // Si les ombres no estan activades o no hi ha llum, no hi ha ombra
    if (!shadow || lights.empty())
    {
        return 0.0f;
    }

    // Calculem la direcció de la llum des del punt
    vec3 lightDir = normalize(lights[0]->vectorL(point));

    // Obtenim la distància des del punt fins a la llum
    float distanceToLight = lights[0]->distanceToLight(point);

    // Creem el raig d'ombra
    vec3 epsilon(0.01f);
    Ray shadowRay(point + epsilon * lightDir, lightDir);
    vector<ShadeInfo> shadowInfos;

    // Si hi ha alguna intersecció entre el punt i la llum, el punt està a l'ombra
    // Tenim en compte el self-shadowing per evitar interseccions amb el mateix objecte
    bool hit = scene->allHits(shadowRay, 0.01f, distanceToLight, shadowInfos);
    if (!hit)
        return 0.0f;

    // Ordenem les interseccions per distància
    std::sort(shadowInfos.begin(), shadowInfos.end(), [](const ShadeInfo &a, const ShadeInfo &b)
              { return a.t < b.t; });

    float shadowFactor = 1.0f;
    float d, dmax;
    for (const auto &shadowInfo : shadowInfos)
    {
        shared_ptr<Transparent> transparent = dynamic_pointer_cast<Transparent>(shadowInfo.mat);
        if (transparent)
        {
            // Calculem la distància des del punt d'intersecció al material fins a la llum
            d = lights[0]->distanceToLight(shadowInfo.p);
            dmax = transparent->getDmax();
            // Ajustem la intensitat de l'ombra segons la transparència del material
            shadowFactor *= (1.0f - glm::min(d / dmax, 1.0f));
        }
        else
        {
            // Si l'objecte no és transparent, aquest bloqueja completament la llum
            shadowFactor = 1.0f;
            break;
        }
    }
    return shadowFactor;
}

vec3 ShadingStrategy::computeColorShadow(vec3 point)
{
    // Si les ombres no estan activades o no hi ha llum, no s'aplica cap filtre
    if (!shadow || lights.empty())
        return vec3(1.0f);

    vec3 lightDir = normalize(lights[0]->vectorL(point));
    float distanceToLight = lights[0]->distanceToLight(point);
    vec3 epsilon(0.01f);
    Ray shadowRay(point + epsilon * lightDir, lightDir);
    vec3 acumulat = vec3(1.0f);

    // Obtenim totes les interseccions entre el punt i la font de llum
    vector<ShadeInfo> shadowInfos;
    bool hit = scene->allHits(shadowRay, 0.01f, distanceToLight, shadowInfos);
    if (!hit)
        return acumulat;

    // Inicialitzem amb color blanc i α acumulada 0
    float acumulada = 0.0f;

    // Recorrem totes les interseccions (suponent que shadowInfos està ordenat per la distància)
    for (const auto &info : shadowInfos)
    {
        // Comprovem si l'objecte intersectat és transparent
        auto transparent = dynamic_pointer_cast<Transparent>(info.mat);
        if (transparent && acumulada < 1.0f)
        {
            float d = lights[0]->distanceToLight(info.p);
            float dmax = transparent->getDmax();
            // Factor de transmissió: com més proper d'usar dmax més petita la transmissió
            float Tt = 1.0f - glm::min(d / dmax, 1.0f);
            acumulat *= (transparent->Kd * Tt);
            acumulada += (1.0f - Tt) * transparent->opacity;
        }
        else
        {
            // Si es troba un objecte opac, es bloqueja completament la llum
            acumulada = 1.0f;
            acumulat = vec3(0.0f);
            break;
        }
    }
    // El color final d'ombra és (1.0 - α_acumulada) * ColorAcumulat
    vec3 finalShadow = (vec3(1.0f) - acumulada) * acumulat;
    return finalShadow;
}

float ShadingStrategy::computePenombraShadow(vec3 point)
{
    // Si no hi ha ombres o font de llum, retornem llum plena
    if (!shadow || lights.empty())
        return 1.0f;

    // Direcció principal de la llum des del punt
    vec3 baseDir = normalize(lights[0]->vectorL(point));
    float maxDist = lights[0]->distanceToLight(point);

    // Calculem dos vectors perpendiculars a la direcció de la llum
    vec3 tangent;
    if (fabs(baseDir.x) <= fabs(baseDir.y) && fabs(baseDir.x) <= fabs(baseDir.z))
        tangent = vec3(0, -baseDir.z, baseDir.y);
    else if (fabs(baseDir.y) <= fabs(baseDir.x) && fabs(baseDir.y) <= fabs(baseDir.z))
        tangent = vec3(-baseDir.z, 0, baseDir.x);
    else
        tangent = vec3(-baseDir.y, baseDir.x, 0);
    tangent = normalize(tangent);
    vec3 bitangent = normalize(cross(baseDir, tangent));

    // Definim els desplaçaments (mostres distribuïdes en forma circular)
    float radius = 0.02f; // Radi del desplaçament per simular la penombra
    std::vector<glm::vec2> sampleOffsets = {
        glm::vec2(0.0f, 0.0f),
        glm::vec2(radius, 0.0f),
        glm::vec2(-radius, 0.0f),
        glm::vec2(0.0f, radius),
        glm::vec2(0.0f, -radius),
        glm::vec2(radius / 1.414f, radius / 1.414f),
        glm::vec2(-radius / 1.414f, radius / 1.414f),
        glm::vec2(radius / 1.414f, -radius / 1.414f),
        glm::vec2(-radius / 1.414f, -radius / 1.414f)};

    float totalFactor = 0.0f;
    // Número de mostres efectives
    size_t sampleCount = sampleOffsets.size();

    // Processar cada desplaçament
    for (const auto &off : sampleOffsets)
    {
        // Desplacem el punt original en direccions tangent i bitangent
        vec3 newOrigin = point + off.x * tangent + off.y * bitangent;
        // Ajust amb epsilon per evitar auto-interseccions
        vec3 epsilon(0.01f);
        Ray sampleRay(newOrigin + epsilon * baseDir, baseDir);

        // Recol·lectem totes les interseccions fins a la font de llum
        vector<ShadeInfo> hits;
        bool interseccio = scene->allHits(sampleRay, 0.01f, maxDist, hits);

        float sampleResult = 1.0f;
        if (interseccio && !hits.empty())
        {
            // Ordenar per distància (si no ho estiguessin ja)
            std::sort(hits.begin(), hits.end(), [](const ShadeInfo &a, const ShadeInfo &b)
                      { return a.t < b.t; });

            // Recorrem les interseccions per acumular el factor de transmissió
            for (const auto &info : hits)
            {
                auto transp = dynamic_pointer_cast<Transparent>(info.mat);
                if (transp)
                {
                    // Calculem un factor en funció de la distància d'intersecció
                    float d = glm::min(lights[0]->distanceToLight(info.p), transp->getDmax());
                    float factor = 1.0f - (d / transp->getDmax());
                    sampleResult *= factor;
                }
                else
                {
                    sampleResult = 0.0f;
                    break;
                }
            }
        }
        totalFactor += sampleResult;
    }

    // Finalment, retornem la mitjana dels factors (clampat entre 0 i 1)
    float penombra = totalFactor / static_cast<float>(sampleCount);
    return glm::clamp(penombra, 0.0f, 1.0f);
}

// Segona implementació, en aquest cas utilitzarem allHits() per a calcular les ombres
/*float ShadingStrategy::computeShadow(vec3 point)
{
    // Si les ombres no estan activades o no hi ha llum, no hi ha ombra
    if (!shadow || lights.empty())
    {
        return false;
    }

    // Calculem la direcció de la llum des del punt
    vec3 lightDir = normalize(lights[0]->vectorL(point));

    // Obtenim la distància des del punt fins a la llum
    float distanceToLight = lights[0]->distanceToLight(point);

    // Creem el raig d'ombra
    Ray shadowRay(point, lightDir);
    vector<ShadeInfo> shadowInfos;

    // Si hi ha alguna intersecció entre el punt i la llum, el punt està a l'ombra
    // Tenim en compte el self-shadowing per evitar interseccions amb el mateix objecte
    if (!scene->allHits(shadowRay, 0.01f, distanceToLight, shadowInfos))
    {
        return false;
    }

    // Calculem la intensitat de l'ombra tenint en compte els materials transparents
    float shadowFactor = 1.0f;
    float d, dmax;

    // Recorrem les interseccions per acumular l'opacitat dels materials transparents
    for (const auto &info : shadowInfos)
    {
        // Obtenim la distància des del punt d'intersecció a la llum
        float d = lights[0]->distanceToLight(info.p);

        // Prova de conversion amb dynamic (utilitzem get() per accedir al punter cru)
        Transparent *transparent = dynamic_cast<Transparent *>(info.mat.get());
        if (transparent)
        {
            float dmax = transparent->getDmax();
            // Calculem el factor d'opacitat complementari
            float factor = 1.0f - glm::min(d / dmax, 1.0f);
            shadowFactor *= factor;
        }
        else
        {
            // Si l'objecte no és transparent, aquest bloqueja completament la llum
            shadowFactor = 0.0f;
            break;
        }
        // Parar el recorregut si la llum ha quedat completament bloquejada
        if (shadowFactor < FLT_EPSILON)
        {
            shadowFactor = 0.0f;
            break;
        }
    }
    return shadowFactor;
}*/