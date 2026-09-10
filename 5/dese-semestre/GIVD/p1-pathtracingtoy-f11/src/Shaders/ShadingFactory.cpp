#include "Shaders/ShadingFactory.hpp"

shared_ptr<ShadingStrategy> ShadingFactory::createShading(SHADING_TYPES t, shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, vec3 lookFrom, bool shadow)
{
    shared_ptr<ShadingStrategy> s;
    switch (t)
    {
    case NORMALSHADING:
        s = make_shared<NormalShading>(scene, lights, lookFrom, shadow);
        break;
    case DIFFUSESHADING:
        s = make_shared<DiffuseShading>(scene, lights, lookFrom, shadow);
        break;
    case TOONSHADING:
        s = make_shared<ToonShading>(scene, lights, lookFrom, shadow);
        break;
    case BLINNPHONGSHADING:
        s = make_shared<BlinnPhongShading>(scene, lights, lookFrom, shadow);
        break;
    default:
        s = nullptr;
        break;
    }
    return s;
}

ShadingFactory::SHADING_TYPES ShadingFactory::getIndexType(shared_ptr<ShadingStrategy> m)
{
    if (dynamic_pointer_cast<NormalShading>(m))
    {
        return SHADING_TYPES::NORMALSHADING;
    }
    else if (dynamic_pointer_cast<DiffuseShading>(m))
    {
        return SHADING_TYPES::DIFFUSESHADING;
    }
    else if (dynamic_pointer_cast<ToonShading>(m))
    {
        return SHADING_TYPES::TOONSHADING;
    }
    else if (dynamic_pointer_cast<BlinnPhongShading>(m))
    {
        return SHADING_TYPES::BLINNPHONGSHADING;
    }
    else
        return SHADING_TYPES::NORMALSHADING;
}
