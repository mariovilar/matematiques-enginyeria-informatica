#pragma once

#include "Materials/Material.hpp"
#include "Materials/Lambertian.hpp"
#include "Materials/Metal.hpp"
#include "Materials/Transparent.hpp"
#include "Materials/MaterialTextura.hpp"

#include <memory>

using namespace std;

class MaterialFactory
{
    MaterialFactory() {};

public:
    typedef enum MATERIAL_TYPES
    {
        LAMBERTIAN,
        METAL,
        TRANSPARENT,
        MATERIALTEXTURA,
    } MATERIAL_TYPES;

    static MaterialFactory &getInstance()
    {
        static MaterialFactory instance;
        return instance;
    }

    shared_ptr<Material> createMaterial(MATERIAL_TYPES t);
    shared_ptr<Material> createMaterial(vec3 a, vec3 d, vec3 s, float beta, float opacity, MATERIAL_TYPES t);
    shared_ptr<Material> createMaterial(const char* filename, MATERIAL_TYPES t);
    MATERIAL_TYPES getIndexType(shared_ptr<Material> m);
};
