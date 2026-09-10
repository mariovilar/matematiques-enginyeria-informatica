#pragma once

#include "Utilities/Ray.hpp"
#include "GeometricObjects/Scene.hpp"
#include "Lights/Light.hpp"
#include "Camera.hpp"
#include "Config.hpp"
#include "Shaders/ShadingFactory.hpp"
#include "Shaders/NormalShading.hpp"
#include "Shaders/DiffuseShading.hpp"
#include "Shaders/ToonShading.hpp"
#include "Shaders/BlinnPhongShading.hpp"

using namespace std;

class Render
{
  public:
    Render() {};
    ~Render() {};

    // Mètode de traçat de rajos
    virtual glm::vec3 tracer(Ray ray) const = 0;

    Render &operator=(const Render &other)
    {
      if (this != &other)
      { // Evitar autoassignació
        // Copiem els atributs
      }
      return *this; // Retornem *this per permetre assignacions encadenades
    }
  protected:
    ShadingFactory *shadingFactory;
    shared_ptr<ShadingStrategy> shadingStrategy;
};
