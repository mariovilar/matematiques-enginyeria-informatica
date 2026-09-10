#include "GeometricObjects/Plane.hpp"

Plane::Plane(vec3 normal, vec3 pass_point) : Object()
{
    this->normal = normalize(normal);
    this->point = pass_point;
}

Plane::Plane(vec3 normal, float d) : Object()
{
    normal = normalize(normal);
    this->normal = normal;
    if (abs(normal.z) > DBL_EPSILON)
        this->point = vec3(0.0, 0.0, -d / normal.z);
    else if (abs(normal.y) > DBL_EPSILON)
        this->point = vec3(0.0, -d / normal.y, 0.0);
    else
        this->point = vec3(-d / normal.x, 0.0, 0.0);
}

bool Plane::hit(Ray &raig, float tmin, float tmax, ShadeInfo &shadeInfo) const
{

    shared_ptr<ShadeInfo> info = make_shared<ShadeInfo>();

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
    float d = -normal[0] * point[0] - normal[1] * point[1] - normal[2] * point[2];

    // 2) Imposem que la recta p+tv compleixi l'eq del pla
    // A(p1 + t*v1) + ... + D = 0
    // Aillem la t
    vec3 rp = raig.origin;
    vec3 vp = raig.direction;
    float temp = -normal[0] * rp[0] - normal[1] * rp[1] - normal[2] * rp[2] - d;
    temp /= normal[0] * vp[0] + normal[1] * vp[1] + normal[2] * vp[2];

    // Retornem false si no estem en el rang demanat
    if (temp < tmax && temp > tmin)
    {
        return false;
    }

    // Omplim el camp de info:
    info->t = temp;
    info->p = raig(info->t);

    // La normal a un pla es la mateixa per tots els punts
    info->normal = normal;
    info->mat = material;
    return true;
}

bool Plane::allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const
{
    shared_ptr<ShadeInfo> info = make_shared<ShadeInfo>();
    bool trobat = hit(r, tmin, tmax, *info);
    if (trobat)
    {
        listShadeInfos.push_back(*info);
        return true;
    }
    return false;
}

void Plane::update(int frame)
{
    // TODO: Cal ampliar-lo per a fer el update del pla, si cal
}

void Plane::aplicaTG(shared_ptr<TG> t)
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
