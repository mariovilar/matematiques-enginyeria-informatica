#pragma once

#include "GeometricObjects/Scene.hpp"
#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Sphere.hpp"
#include "Lights/Light.hpp"
#include "Lights/PointLight.hpp"
#include "Utilities/ShadeInfo.hpp"
#include "Materials/Material.hpp"

#include <algorithm>

class ShadingStrategy
{
protected:
    shared_ptr<Scene> scene;
    vec3 lookFrom;
    bool shadow;
    vector<shared_ptr<Light>> lights;

public:
    virtual vec3 shading(ShadeInfo &info)
    {
        return vec3(0.0, 0.0, 0.0);
    };

    virtual vec3 shading(vector<shared_ptr<ShadeInfo>> info)
    {
        return vec3(0.0, 0.0, 0.0);
    };

    // Operador d'assignació
    ShadingStrategy &operator=(const ShadingStrategy &other)
    {
        if (this != &other)
        {                        // Evitar autoassignació
            scene = other.scene; // Copiem el shared_ptr (incrementa el comptador de referències)
            lookFrom = other.lookFrom;
            shadow = other.shadow;
            lights = other.lights;
        }
        return *this; // Retornem *this per permetre assignacions encadenades
    }

    // Calcula si el punt "point" és a l'ombra s
    float computeShadow(vec3 point);
    vec3 computeColorShadow(vec3 point);
    float computePenombraShadow(vec3 point);
    virtual ~ShadingStrategy() {};
};