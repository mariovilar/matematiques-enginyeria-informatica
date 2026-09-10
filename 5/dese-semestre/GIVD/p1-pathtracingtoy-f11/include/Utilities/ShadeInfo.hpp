#pragma once

#include <memory>

#include "glm/glm.hpp"
#include "Materials/Material.hpp"

using namespace glm;
using namespace std;

class ShadeInfo {
public:
    float                t;          // t del raig on hi ha intersecció
    vec3                 p;          // punt del raig on hi ha la intersecció
    vec3                 normal;     // normal en el punt d'intersecció
    shared_ptr<Material> mat;        // material de l'objecte que s'ha intersectat
    vec2                 uv;         // punt 2D per la projeccio de la textura
    shared_ptr<Ray>      ray;        // raig que ha fet la intersecció

    ShadeInfo():
        t(std::numeric_limits<float>::infinity()),
        p(0.0f),
        normal(0.0f), mat(nullptr),
        uv(0.0f), ray() {}

    // "operator =" per la classe  ShadeInfo
    ShadeInfo &operator =(const ShadeInfo &rhs) {
      p = rhs.p;
      mat = rhs.mat;
      normal = rhs.normal;
      t = rhs.t;
      uv = rhs.uv;
      ray = rhs.ray;
      return *this;
    }
};
