#pragma once

#include <vector>

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <glm/glm.hpp>
#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Face.hpp"

using namespace std;

class Mesh : public Object {
public:
    Mesh() {};
    Mesh(const string &fileName);

    virtual bool hit (Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;
    virtual bool allHits(Ray& r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const override;

    virtual void update(int nframe) override;
    virtual void aplicaTG(shared_ptr<TG> tg) override;

    virtual ~Mesh();

private:
    string nom;
    vector<Face> cares; // facees o cares de l'objecte
    vector<vec4> vertexs; // vertexs de l'objecte sense repetits

    void load(const std::string & filename);
    void makeTriangles();
};
