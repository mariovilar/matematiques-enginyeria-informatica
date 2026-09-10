#pragma once

#include "Object.hpp"
#include "Mesh.hpp"
#include "Cub.hpp"

class ObjectFactory
{
    ObjectFactory(){};

public:
    typedef enum  {
        BOX,
        MESH
    } OBJECT_TYPES;

    static ObjectFactory& getInstance() {
        static ObjectFactory instance;
        return instance;
    }

    shared_ptr<Object> createObject(OBJECT_TYPES t);
    OBJECT_TYPES getIndexType (shared_ptr<Object> l);

};