#include "Materials/MaterialFactory.hpp"

shared_ptr<Material> MaterialFactory::createMaterial(MATERIAL_TYPES t)
{
    shared_ptr<Material> m;
    switch (t)
    {
    case LAMBERTIAN:
        m = make_shared<Lambertian>();
        break;
    case METAL:
        m = make_shared<Metal>();
        break;
    case TRANSPARENT:
        m = make_shared<Transparent>();
        break;
    case MATERIALTEXTURA:
        m = make_shared<MaterialTextura>();
        break;
    default:
        break;
    }
    return m;
}

shared_ptr<Material> MaterialFactory::createMaterial(vec3 a, vec3 d, vec3 s, float beta, float opacity, MATERIAL_TYPES t)
{
    shared_ptr<Material> m;
    switch (t)
    {
    case LAMBERTIAN:
        m = make_shared<Lambertian>(a, d, s, beta, opacity);
        break;
    case METAL:
        m = make_shared<Metal>(a, d, s, beta);
        break;
    case TRANSPARENT:
        m = make_shared<Transparent>(a, d, s, beta, opacity);
        break;
    case MATERIALTEXTURA:
        m = make_shared<MaterialTextura>();
        break;
    default:
        break;
    }
    return m;
}

shared_ptr<Material> MaterialFactory::createMaterial(const char* filename, MATERIAL_TYPES t)
{
    shared_ptr<Material> m;
    switch (t)
    {
    case LAMBERTIAN:
        m = make_shared<Lambertian>();
        break;
    case METAL:
        m = make_shared<Metal>();
        break;
    case TRANSPARENT:
        m = make_shared<Transparent>();
        break;
    case MATERIALTEXTURA:
        m = make_shared<MaterialTextura>(filename);
        break;
    default:
        break;
    }
    return m;
}

MaterialFactory::MATERIAL_TYPES MaterialFactory::getIndexType(shared_ptr<Material> m)
{
    if (dynamic_pointer_cast<Lambertian>(m))
    {
        return MATERIAL_TYPES::LAMBERTIAN;
    }
    else if (dynamic_pointer_cast<Metal>(m))
    {
        return MATERIAL_TYPES::METAL;
    }
    else if (dynamic_pointer_cast<Transparent>(m))
    {
        return MATERIAL_TYPES::TRANSPARENT;
    }
    else if (dynamic_pointer_cast<MaterialTextura>(m))
    {
        return MATERIAL_TYPES::MATERIALTEXTURA;
    }
    return MATERIAL_TYPES::LAMBERTIAN;
}
