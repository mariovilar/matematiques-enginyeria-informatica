#pragma once

#include <vector>

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <glm/glm.hpp>

#include "Object.hpp"
#include "Face.hpp"

using namespace std;

class Mesh : public Object
{
public:
    Mesh() {};
    Mesh(const string &fileName);

    virtual ~Mesh();
    void make();

protected:
    string nom; // nom del fitxer
    vector<Face> cares; // facees o cares de l'objecte
    vector<vec4> vertexs; // vertexs de l'objecte sense repetits
    vector<vec4> normalsVertexs; // normals de l'objecte sense repetits
    vector<vec2> textVertexs; // coordenades de textures sense repetits
   
private:
    void load(const std::string &fileName);

};

