#pragma once

#include "Object.hpp"

using namespace glm;

class Cub : public Object
{
public:
    Cub();
    Cub(int an, int al, int profu, float x0, float y0, float z0);
    ~Cub();
    void make();
    
   
private:
    void initCub();
    void quad(int a, int b, int c, int d);

    int a; // amplada
    int h; // alcada
    int p; // profunditat
    GLdouble xorig, yorig, zorig;

    glm::vec4 vertices[8];      // 8 vertexs del cub
    glm::vec4 vertex_colors[8]; // 8 colors RGBA associats a cada vertex
    
};