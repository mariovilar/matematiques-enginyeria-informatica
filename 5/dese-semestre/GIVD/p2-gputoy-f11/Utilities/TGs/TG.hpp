#pragma once

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"

using namespace glm;

class TG {
public:
    glm::mat4 transform;
    TG() {
        transform = glm::mat4(1.0f);
    };
    ~TG() {};
    virtual glm::mat4 getTG() { return transform; };
};
