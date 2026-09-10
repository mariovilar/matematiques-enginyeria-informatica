#include "ObjectFactory.hpp"


// Trobaràs l'enumeració d'OBJECT_TYPES en el fitxer FactoryObject.hh
shared_ptr<Object> ObjectFactory::createObject(OBJECT_TYPES t)
{
    shared_ptr<Object> o;
    switch (t) {
    case MESH:
        o = make_shared<Mesh>();
        break;
    case BOX:
        o = make_shared<Cub>();
        break;
    
    default:
        break;
    }

    return o;
}

ObjectFactory::OBJECT_TYPES ObjectFactory::getIndexType(shared_ptr<Object> l) {
    if (dynamic_pointer_cast<Mesh>(l) != nullptr) {
        return OBJECT_TYPES::MESH;

    } else if (dynamic_pointer_cast<Cub>(l) != nullptr) {
        return OBJECT_TYPES::BOX;
    }
    return OBJECT_TYPES::MESH;
}


