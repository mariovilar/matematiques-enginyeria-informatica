#include "Lights/PointLight.hpp"

PointLight::PointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is) : Light(Ia, Id, Is)
{
    this->pos = posicio;
    this->a = 0.0f;
    this->b = 0.0f;
    this->c = 0.0f;
}

PointLight::PointLight(vec3 posicio, vec3 Ia, vec3 Id, vec3 Is, float a, float b, float c) : Light(Ia, Id, Is)
{
    this->pos = posicio;
    this->a = a;
    this->b = b;
    this->c = c;
}

vec3 PointLight::vectorL(vec3 point)
{
    return normalize(pos - point);
}

float PointLight::attenuation(vec3 point)
{
    if (abs(a) < DBL_EPSILON && abs(b) < DBL_EPSILON && abs(c) < DBL_EPSILON)
    {
        // Si tots els coeficients son 0 considerem que no hi ha atenuacio
        return 1.0f;
    }
    // Calculem la distancia entre el punt i la posicio de la llum
    float d = distance(point, pos);
    return 1.0f / (c * d * d + b * d + a);
}

float PointLight::distanceToLight(vec3 point)
{
    return distance(point, pos);
}
