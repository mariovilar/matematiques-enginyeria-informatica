#pragma once

#include "Renders/Render.hpp"
#include "GeometricObjects/Scene.hpp"
#include "Lights/Light.hpp"
#include "Camera.hpp"
#include "Config.hpp"

class Raycasting : public Render
{
    shared_ptr<Scene> scene;
    vector<shared_ptr<Light>> lights;
    shared_ptr<Camera> camera;
    shared_ptr<Config> config;

public:
    Raycasting(shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, shared_ptr<Camera> camera, shared_ptr<Config> config)
    {
        this->scene = scene;
        this->lights = lights;
        this->camera = camera;
        this->config = config;
        this->shadingFactory = &ShadingFactory::getInstance();
        this->shadingStrategy = shadingFactory->createShading((ShadingFactory::SHADING_TYPES)(config->selectedShader), scene, lights, camera->position, config->shadowMode);
    };

    virtual ~Raycasting() {};

    virtual glm::vec3 tracer(Ray ray) const override;
};