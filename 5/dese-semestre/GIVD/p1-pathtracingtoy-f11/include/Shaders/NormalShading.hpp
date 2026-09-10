#pragma once

#include "Shaders/ShadingStrategy.hpp"

class NormalShading: public ShadingStrategy {    
public:
    NormalShading(shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, vec3 lookFrom, bool shadow) {
        this->scene = scene;
        this->lookFrom = lookFrom;
        this->shadow = shadow;
        this->lights = lights;
    };
    virtual vec3 shading(ShadeInfo &info) override;
    virtual vec3 shading(vector<shared_ptr<ShadeInfo>> info) override;
    ~NormalShading(){};
};
