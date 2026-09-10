#ifndef RAY_HPP
#define RAY_HPP

#include <glm/glm.hpp>

class Ray {
public:
    glm::vec3 origin;      // Punt d'origen del raig
    glm::vec3 direction;   // Direcció del raig (normalitzada)
    float tmin, tmax;      // Distàncies mínima i màxima per a la intersecció

    // Constructor
    Ray() {}
    Ray(const glm::vec3& o, const glm::vec3& d, float t_min_=0.01f, float t_max_=std::numeric_limits<float>::infinity())
        : origin(o), direction(glm::normalize(d)), tmin(t_min_),
        tmax(t_max_) {}

    // retorna el punt del raig en en temps/lambda t 
    glm::vec3 operator() (const float &t) const {
        return origin + direction*t;
    }
};

#endif // RAY_HPP
