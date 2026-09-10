#pragma once

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

using namespace glm;

class Camera {
public:
    // Atributs de la càmera
    glm::vec3 position;    // Posició de la càmera
    glm::vec3 direction;   // Direcció cap a on apunta
    glm::vec3 vrp;         // Punt cap a on apunta
    glm::vec3 up;          // Vector cap amunt
    float fov;             // Angle de visió (Field of View)

    // Constructor amb paràmetres
    Camera(glm::vec3 pos = glm::vec3(0.0f, 0.0f, 3.0f), 
           glm::vec3 vrp = glm::vec3(0.0f, 0.0f, -1.0f),
           glm::vec3 upVector = glm::vec3(0.0f, 1.0f, 0.0f),
           float fieldOfView = 90.0f);

    // Obtenir matriu de vista
    glm::mat4 getViewMatrix() const;

    // Ajustar posició
    void move(const glm::vec3& offset);

    // Ajustar direcció
    void rotate(float pitch, float yaw);
};
