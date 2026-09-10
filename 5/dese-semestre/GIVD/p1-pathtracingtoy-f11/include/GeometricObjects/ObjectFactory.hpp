#pragma once

#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Sphere.hpp"
#include "GeometricObjects/Plane.hpp"
#include "GeometricObjects/FittedPlane.hpp"
#include "GeometricObjects/Box.hpp"

class ObjectFactory {
    ObjectFactory(){};
public:
    typedef enum  {
        SPHERE,
        PLANE,
        BOX,
        TRIANGLE,
        MESH,
        FITTEDPLANE
    } OBJECT_TYPES;

    static ObjectFactory& getInstance() {
        static ObjectFactory instance;
        return instance;
    }

    shared_ptr<Object> createObject(OBJECT_TYPES t);

    OBJECT_TYPES getIndexType (shared_ptr<Object> l);
};
