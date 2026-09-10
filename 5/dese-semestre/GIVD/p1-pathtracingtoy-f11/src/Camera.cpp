#include "Camera.hpp"

Camera::Camera(vec3 obs, vec3 vrp, vec3 up, float fielOfView) {
    this->position = obs;
    this->vrp = vrp;
    this->fov = fielOfView;
    this->up = up;
    this->direction = glm::normalize(vrp-obs);
}

// Retorna la matriu de vista basada en la posició, direcció i vector up
glm::mat4 Camera::getViewMatrix() const {
    return glm::lookAt(position, position + direction, up);
}

// Mou la càmera segons un desplaçament donat
void Camera::move(const glm::vec3& offset) {
    position += offset;
}

// Rota la càmera ajustant els angles de pitch (inclinació) i yaw (gir horitzontal)
void Camera::rotate(float pitch, float yaw) {
    // Calcular nova direcció basant-se en els angles
    glm::vec3 newDirection;
    newDirection.x = cos(glm::radians(yaw)) * cos(glm::radians(pitch));
    newDirection.y = sin(glm::radians(pitch));
    newDirection.z = sin(glm::radians(yaw)) * cos(glm::radians(pitch));
    direction = glm::normalize(newDirection);
}
