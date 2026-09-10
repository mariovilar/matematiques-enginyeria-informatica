#pragma once

#include <stdlib.h>
#include <GL/glew.h>

using namespace std;

class GPUConnectable
{
public:
    GPUConnectable() {};
    ~GPUConnectable() {};

    virtual void toGPU(GLuint p) = 0;
    virtual void draw() {};

protected:
    GLuint program;
};