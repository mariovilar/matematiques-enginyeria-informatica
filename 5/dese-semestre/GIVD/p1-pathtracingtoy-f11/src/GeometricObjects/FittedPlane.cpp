#include "GeometricObjects/FittedPlane.hpp"
#include <iostream>

FittedPlane::FittedPlane(vec3 normal, vec3 pass_point, vec2 pmin, vec2 pmax, bool center) : Plane(normal, pass_point){
    this->pmin = pmin;
    this->pmax = pmax;
    this->center = center;
}

FittedPlane::FittedPlane(vec3 normal, float d, vec2 pmin, vec2 pmax, bool center) : Plane(normal, d){
    this->pmin = pmin;
    this->pmax = pmax;
    this->center = center;
}

bool FittedPlane::hit(Ray &raig, float tmin, float tmax, ShadeInfo &shadeInfo) const
{
    // Comprovem interseccio entre el pla i el raig

    // Comprovem si el normal al pla i el raig son ortogonals.
    // En aquest cas son paralels i no hi ha interseccio

    if (abs(dot(raig.direction, normal)) < DBL_EPSILON)
    {
        return false;
    }

    // En els altres casos hi haurà interseccio (si estem en el rang de min/max).
    // Cal omplir la informació del hit.

    // PLA: Ax+By+Cz+D=0
    // on A,B,C = normal

    // 1) Calculem la D = -Ax-By-Cz
    float d = -dot(normal, point);

    // 2) Imposem que la recta p+tv compleixi l'eq del pla
    // A(p1 + t*v1) + ... + D = 0
    // Aillem la t
    vec3 rp = raig.origin;
    vec3 vp = raig.direction;
    float temp = -(dot(normal, rp) + d) / dot(normal, vp);

    // Retornem false si no estem en el rang demanat
    if (temp > tmax && temp < tmin)
        return false;

    shadeInfo.t = temp;
    shadeInfo.p = raig(temp);
    shadeInfo.normal = normal;
    shadeInfo.mat = material;

    // Calcular les coordenades de textura
    float x = rp[0] + temp * vp[0];
    float y = rp[1] + temp * vp[1];
    float maxX, maxY, minX, minY;

    if(center) {
        maxX = point[0] + pmax[0];
        maxY = point[1] + pmax[1];
        minX = point[0] + pmin[0];
        minY = point[1] + pmin[1];
    } 
    else {
        maxX = pmax[0];
        maxY = pmax[1];
        minX = pmin[0];
        minY = pmin[1];
    }

    // Comprovem si les coordenades cau dins dels límits especificats
    if (x < minX || x > maxX || y < minY || y > maxY)
        return false;

    // Calcular les coordenades UV en base als límits
    float u = (x - minX) / (maxX - minX);
    float v = (y - minY) / (maxY - minY);
    shadeInfo.uv = vec2(u, v);

    return true;
}

bool FittedPlane::allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const
{
    return Plane::allHits(r, tmin, tmax, listShadeInfos);
}

void FittedPlane::update(int frame)
{
    // TODO: Cal ampliar-lo per a fer el update del pla, si cal
}

void FittedPlane::aplicaTG(shared_ptr<TG> t)
{
    if (dynamic_pointer_cast<shared_ptr<TranslateTG>>(t))
    {

        // Nomes movem el punt de pas
        vec4 newp(this->point, 1.0);
        newp = t->getTG() * newp;
        this->point.x = newp.x;
        this->point.y = newp.y;
        this->point.z = newp.z;
    }
}
