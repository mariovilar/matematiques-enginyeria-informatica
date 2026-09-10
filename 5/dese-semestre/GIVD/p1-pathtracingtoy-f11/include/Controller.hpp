#pragma once

#include <memory>
#include <vector>
#include "World.hpp"
#include "Config.hpp"
#include <GLFW/glfw3.h>
#include <chrono>

using namespace std;

class Controller
{
private:
    static Controller *instancePtr;

    shared_ptr<World> world;
    shared_ptr<Config> setup;

    // Frame buffer
    shared_ptr<Image> framebuffer;

    float renderTimeMs = 0.0f; // Temps de renderització en ms

    // Default constructor private
    Controller();

public:
    // deleting copy constructor
    Controller(const Controller &obj) = delete;

    static Controller *getInstance();

    shared_ptr<World> getWorld() { return world; }
    float getRenderTime() { return renderTimeMs; }

    void setWorld(shared_ptr<World> w) { world = w; }
    void setSetup(shared_ptr<Config> s)
    {
        setup = s;
        if (world)
            world->setConfig(s);
    }

    shared_ptr<Config> getSetup() { return setup; }

    bool needsToRender = true;

    // bool createWorld();
    // bool createWorld(int nFrames);
    void setLightGlobal(vec3 lightGlobal);
    void generateRandomSpheres(int numSpheres);
    void setNumSamples(int numSamples);
    void renderWorld();
    void requestRender();
    void refresh();
    void changeCamera(glm::vec3 obs, glm::vec3 vrp, glm::vec3 up, float fov);

    void saveImage(const char *filename);
    void saveAnimation(const char *animationBaseName);
    void update(int nframe) { world->update(nframe); };
};