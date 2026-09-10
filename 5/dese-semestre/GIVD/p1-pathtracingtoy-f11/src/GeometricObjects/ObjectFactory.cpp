#include "GeometricObjects/ObjectFactory.hpp"

// TODO Fase 1: Crea objectes de més tipus
// Trobaràs l'enumeració d'OBJECT_TYPES en el fitxer FactoryObject.hh
shared_ptr<Object> ObjectFactory::createObject(OBJECT_TYPES t)
{
    shared_ptr<Object> o;
    switch (t) {
    case SPHERE:
        o = make_shared<Sphere>();
        break;
    case PLANE:
        o = make_shared<Plane>();
        break;
    case BOX:
        o = make_shared<Box>();
        break;
    case FITTEDPLANE:
        o = make_shared<FittedPlane>();
        break;
    default:
        break;
    }

    return o;
}

ObjectFactory::OBJECT_TYPES ObjectFactory::getIndexType(shared_ptr<Object> l) {
    if (dynamic_pointer_cast<Sphere>(l) != nullptr) {
        return OBJECT_TYPES::SPHERE;

    } 
    else if (dynamic_pointer_cast<Plane>(l) != nullptr) {
        return OBJECT_TYPES::PLANE;
    } 
    else if (dynamic_pointer_cast<Box>(l) != nullptr) {
        return OBJECT_TYPES::BOX;
    }
    else if (dynamic_pointer_cast<FittedPlane>(l) != nullptr) {
        return OBJECT_TYPES::FITTEDPLANE;
    }
    return OBJECT_TYPES::SPHERE;
}
