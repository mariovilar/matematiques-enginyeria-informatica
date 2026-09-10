#pragma once

#include <vector>
#include "glm/glm.hpp"

using namespace std;
using namespace glm;

// Face - representa una cara d'un objecte 3D
class Face
{
 public:
    Face();

    // constructor a partir de 3 o 4 indexs a vertex
    Face(int i1, int i2, int i3, int i4=-1);

    vector<int> idxVertices;  // vector amb els indexs dels vertexs de la cara
    vector<int> idxNormals;
    vector<int> idxTextures;

    vec3 normal;
    void calculaNormal(vector<vec4> &);
};


