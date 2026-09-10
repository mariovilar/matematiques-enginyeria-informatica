#pragma once

#include <string>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "Utilities/GPUConnectable.hpp"

using namespace glm;
using namespace std;

class GPUMaterial : public GPUConnectable
{
public:

    GPUMaterial();
    GPUMaterial(vec3 d);
    GPUMaterial(vec3 a, vec3 d, vec3 s, float shininess);
    
    virtual ~GPUMaterial() {};

    void toGPU(GLuint program) override;
    void draw() override {};

    vec3 Ka;
    vec3 Kd;
    vec3 Ks;

    float shininess;
    float opacity;     // opacity es la fraccio de 0..1 (0 és totalment transparent, 1 és totalment opac)

};


