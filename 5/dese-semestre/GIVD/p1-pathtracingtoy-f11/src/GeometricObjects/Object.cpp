#include "GeometricObjects/Object.hpp"
#include "Object.hpp"

Object::Object()
{
    material = nullptr;
}

void Object::setMaterial(shared_ptr<Material> m)
{
    material = m;
}

shared_ptr<Material> Object::getMaterial()
{
    return material;
}