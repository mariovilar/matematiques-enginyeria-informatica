#pragma once

#include "Utilities/TGs/TG.hpp"

class Rotate : public TG {
public:
    glm::vec3 rotation;
    Rotate(glm::vec3 rot) {
        rotation = rot;
        // Com que la matriu transform es reinicia a la identitat (glm::mat4(1.0f)) 
        // cada vegada que es mou el ratolí (o hauria de fer-ho dins de cada mode), 
        // el que s'hauria de fer és aplicar la translació total acumulada (xTras, yTras) 
        // directament a la matriu identitat.
        transform = glm::rotate(transform, rotation.x, glm::vec3(1.0f, 0.0f, 0.0f));
        transform = glm::rotate(transform, rotation.y, glm::vec3(0.0f, 1.0f, 0.0f));
        transform = glm::rotate(transform, rotation.z, glm::vec3(0.0f, 0.0f, 1.0f));
    };
    virtual ~Rotate() {};
};