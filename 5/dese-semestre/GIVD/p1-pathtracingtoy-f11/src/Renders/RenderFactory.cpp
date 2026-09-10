#include "Renders/RenderFactory.hpp"

shared_ptr<Render> RenderFactory::createRender(RENDER_TYPES t, shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, shared_ptr<Camera> camera, shared_ptr<Config> config)
{
    shared_ptr<Render> r;
    switch (t)
    {
    case RAYCASTING:
        r = make_shared<Raycasting>(scene, lights, camera, config);
        break;
    case RAYTRACING:
        r = make_shared<Raytracing>(scene, lights, camera, config);
        break;
    default:
        break;
    }
    return r;
}

RenderFactory::RENDER_TYPES RenderFactory::getIndexType(shared_ptr<Render> r)
{
    if (dynamic_pointer_cast<Raycasting>(r))
    {
        return RENDER_TYPES::RAYCASTING;
    }
    else if (dynamic_pointer_cast<Raytracing>(r))
    {
        return RENDER_TYPES::RAYTRACING;
    }
    return RENDER_TYPES::RAYCASTING;
}