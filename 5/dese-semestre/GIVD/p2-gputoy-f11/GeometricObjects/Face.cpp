#include "Face.hpp"
#include <cmath>

Face::Face()
{
    idxVertices.clear();
    idxNormals.clear();
    idxTextures.clear();
    normal = vec3(0.0, 0.0, 0.0);
}

Face::Face(int i1, int i2, int i3, int i4)
{
    idxVertices.push_back(i1);
    idxVertices.push_back(i2);
    idxVertices.push_back(i3);
    if (i4!=-1) idxVertices.push_back(i4);
}

