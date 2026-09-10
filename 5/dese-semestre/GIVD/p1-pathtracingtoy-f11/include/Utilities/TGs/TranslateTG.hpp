#pragma once

#include "Utilities/TGs/TG.hpp"

class TranslateTG : public TG {
public:
    glm::vec3 traslation;

    TranslateTG(glm::vec3 trasl) {
        traslation = trasl;
        matTG = glm::translate(matTG, traslation);
    };

    virtual ~TranslateTG() {};
};
