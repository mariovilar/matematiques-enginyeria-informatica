#include "Renders/Raycasting.hpp"

vec3 Raycasting::tracer(Ray ray) const
{
    vec3 pixelColor(-1.0f);

    shared_ptr<ShadeInfo> shadeInfo = make_shared<ShadeInfo>();
    shadeInfo->ray = make_shared<Ray>(ray);

    // Obtenim el shading strategy
    ShadingFactory ShadingFactory = ShadingFactory::getInstance();
    shared_ptr<ShadingStrategy> shadingStrategy = ShadingFactory.createShading((ShadingFactory::SHADING_TYPES)(config->selectedShader), scene, lights, camera->position, config->shadowMode);

    // Si hi ha bounding box pero el raig no interseca, retornem false
    bool shouldTestScene = !config->boundingBox || (scene->boundingBox && scene->boundingBox->hit(ray, 0.01f, FLT_MAX, *shadeInfo));

    // Si s'ha intersecat amb la bounding box o no s'utilitza, comprova si hi ha intersecció amb l'escena
    if (shouldTestScene && scene->hit(ray, 0.01f, FLT_MAX, *shadeInfo))
    {
        // En aquest cas, el color del pixel és el color calculat
        pixelColor = shadingStrategy->shading(*shadeInfo);
    }

    return pixelColor;
}