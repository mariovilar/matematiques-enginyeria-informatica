#pragma once

#include <random> // Llibreria per generar números aleatoris
#include <memory>

#include "GeometricObjects/Scene.hpp"
#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Sphere.hpp"

#include "Lights/Light.hpp"
#include "Lights/PointLight.hpp"

#include "Camera.hpp"
#include "Config.hpp"

#include "Utilities/ShadeInfo.hpp"

#include "Utilities/Image.hpp"

#include "Renders/RenderFactory.hpp"

#include <stdlib.h>
#include <iostream>

class World
{
public:
    typedef enum
    {
        VIRTUALWORLD,
        TEMPORALVW
    } DATA_TYPES;

    // Escena que conte els objectes
    shared_ptr<Scene> scene;

    // Vector de llums contingudes la mon
    vector<shared_ptr<Light>> lights;

    // Camera
    shared_ptr<Camera> camera;

    // Configuracio de la visualitzacio
    shared_ptr<Config> setup;

    World();

    virtual ~World() {};

    void setConfig(shared_ptr<Config> s)
    {
        setup = s;
        refreshProperties();
    }
    void setCamera(shared_ptr<Camera> c) { camera = c; }
    void addLight(shared_ptr<Light> l) { lights.push_back(l); }
    void addObject(shared_ptr<Object> obj) { scene->addObject(obj); }
    void setScene(shared_ptr<Scene> s) { scene = s; }

    void setLightGlobal(vec3 lightGlobal);
    void generateRandomSpheres(int numSpheres);
    void setNumSamples(int numSamples);

    // Funció que renderitza el mon en un framebuffer ja allocatat i el refresca
    void renderWorld(Image &framebuffer, shared_ptr<Config> config);

    void clear()
    {
        scene->clear();
        lights.clear();
    }

    void refreshProperties();
    void changeCamera(glm::vec3 obs, glm::vec3 vrp, glm::vec3 up, float fov);
    void generateAnimation(int numFrames);
    void update(int nframe);
};
