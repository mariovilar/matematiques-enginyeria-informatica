#include "Renders/Raytracing.hpp"

vec3 Raytracing::tracer(Ray ray) const
{
    vec3 pixelColor(0.0f);

    // Número de rajos per píxel definit a la configuració
    int numSamples = config->numSamples;

    // Si només llancem un raig no fem el ballRand
    if (numSamples == 1)
    {
        pixelColor = tracerRecursiu(ray, 0);
    }
    else
    {
        // Llancem multiples rajos
        for (int i = 0; i < numSamples; i++)
        {
            // Movem una miqueta el raig original
            Ray sampleRay = ray;
            sampleRay.direction = glm::normalize(sampleRay.direction + glm::ballRand(0.001f));

            // La crida recursiva es fa a partir del nou raig
            pixelColor += tracerRecursiu(sampleRay, 0);
        }
    }

    // Fem el promig de tots els rajos
    pixelColor /= float(numSamples);
    pixelColor = glm::clamp(pixelColor, vec3(0.0f), vec3(1.0f));

    return pixelColor;
}

vec3 Raytracing::tracerRecursiu(Ray ray, int depth) const
{
    vec3 pixelColor(0.0f);

    shared_ptr<ShadeInfo> shadeInfo = make_shared<ShadeInfo>();
    shadeInfo->ray = make_shared<Ray>(ray);

    // Si hi ha bounding box pero el raig no interseca, retornem false
    bool shouldTestScene = !config->boundingBox || (scene->boundingBox && scene->boundingBox->hit(ray, 0.01f, FLT_MAX, *shadeInfo));

    // Si s'ha intersecat amb la bounding box o no s'utilitza, comprova si hi ha intersecció amb l'escena
    if (shouldTestScene && scene->hit(ray, 0.01f, FLT_MAX, *shadeInfo))
    {

        // New color
        vec3 newColor = shadingStrategy->shading(*shadeInfo);

        // Si alcanzamos la profundidad máxima, agafem el color del píxel que trobi el raig
        if (depth >= config->MAXDEPTH)
        {
            return newColor;
        }
        else
        {
            // Cridem a evaluate() del material per calcular el raig secundari
            Ray scatteredRay;
            vec3 scatteredColor;

            if (shadeInfo->mat && shadeInfo->mat->evaluate(ray, shadeInfo->t, shadeInfo->normal, scatteredColor, scatteredRay))
            {
                // Si evaluate retorna true, calculem la contribució recursiva del raig secundari
                newColor *= shadeInfo->mat->opacity;
                newColor += scatteredColor * tracerRecursiu(scatteredRay, depth + 1);
            }
            pixelColor += newColor;
        }
    }
    else
    {
        bool paintBackground = config->depthShading || (!config->depthShading && depth == 0);
        if (paintBackground)
        {
            // Si hi ha textura de fons
            if (config->backgroundMode == 1 && config->background)
            {
                float w = (float)config->background->getWidth();
                float h = (float)config->background->getHeight();
                float a = (ray.direction.x + 1) * 0.5 * w;
                float b = (ray.direction.y + 1) * 0.5 * h;
                // Inverteix la coordenada Y perquè la imatge es carrega al revés
                pixelColor += config->background->getPixelColor(a, b);
            }
            else
            {
                pixelColor += config->backgroundColor;
            }
        }
        else
        {
            pixelColor += scene->lightGlobal;
        }
    }

    return glm::clamp(pixelColor, vec3(0.0f), vec3(1.0f));
}