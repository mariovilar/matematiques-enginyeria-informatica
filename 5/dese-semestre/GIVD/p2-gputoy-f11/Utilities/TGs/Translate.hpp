#pragma once

#include "Utilities/TGs/TG.hpp"

class Translate : public TG {
public:
    glm::vec3 traslation;
    Translate(glm::vec3 trasl) {
        traslation = trasl;
        transform = glm::translate(transform, traslation);
    };

    virtual ~Translate() {};
};