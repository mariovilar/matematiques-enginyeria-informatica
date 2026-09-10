#pragma once

#include "Renders/Render.hpp"
#include "Renders/Raycasting.hpp"
#include "Renders/Raytracing.hpp"

class RenderFactory
{
    RenderFactory() {};

public:
    typedef enum
    {
        RAYCASTING,
        RAYTRACING,
        PATHTRACING
    } RENDER_TYPES;

    static RenderFactory &getInstance()
    {
        static RenderFactory instance;
        return instance;
    }

    shared_ptr<Render> createRender(RENDER_TYPES t, shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, shared_ptr<Camera> camera, shared_ptr<Config> config);
    RENDER_TYPES getIndexType(shared_ptr<Render> r);
};