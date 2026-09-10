#pragma once

#include <vector>
#include "glm/glm.hpp"

using namespace std;
using namespace glm;

typedef vec4 Vertices;
// Face - representa una cara d'un objecte 3D
class Face {
 public:
    Face();

    // Constructor a partir de 3 o 4 indexs a vertex
    Face(int i1, int i2, int i3, int i4=-1);

    vector<int> idxVertices;  // vector amb els indexs dels vertexs de la cara
};
