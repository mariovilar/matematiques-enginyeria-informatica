#include "GeometricObjects/Box.hpp"
#include <math.h>

using namespace glm;

Box::Box()
{
    vertexs = vector<vec3>(
        {vec3(-1.0, -1.0, -1.0),
         vec3(1.0, 1.0, 1.0)});
}

Box::Box(vector<vec3> vertexs)
{
    this->vertexs = vertexs;
}

Box::Box(vector<vec3> vertexs, shared_ptr<Material> material)
{
    this->vertexs = vertexs;
    this->material = material;
}

Box::~Box()
{
    if (vertexs.size() > 0)
        vertexs.clear();
}

bool Box::hit(Ray &r, float tmin, float tmax, ShadeInfo &shadeInfo) const
{
    // Variables
    float xmin, ymin, zmin, xmax, ymax, zmax;
    float dx, dy, dz;
    float px, py, pz;
    float txmin, txmax, tymin, tymax, tzmin, tzmax;
    float tenterx, texitx, tentery, texity, tenterz, texitz;
    float tenter, texit;
    vec3 normal;

    // Assignacions, suposant que els vertexs estan ordenats
    xmin = vertexs[0].x;
    ymin = vertexs[0].y;
    zmin = vertexs[0].z;
    xmax = vertexs[1].x;
    ymax = vertexs[1].y;
    zmax = vertexs[1].z;
    dx = r.direction.x;
    dy = r.direction.y;
    dz = r.direction.z;
    px = r.origin.x;
    py = r.origin.y;
    pz = r.origin.z;


    // Considerar les observacions fetes a la pàgina 222 del pdf resum_Raytracing.pdf
    // Per les quals no cal considerar el cas en què el raig sigui paral·lel a un eix com un cas especial
    // Però si cal considerar que el raig pot venir per sota i no per dalt

    float a = 1.0 / dx;
    float b = 1.0 / dy;
    float c = 1.0 / dz;

    if(a >= 0) {
        txmin = (xmin - px) * a;
        txmax = (xmax - px) * a;
    } else {
        txmin = (xmax - px) * a;
        txmax = (xmin - px) * a;
    }
    if (b >= 0) {
        tymin = (ymin - py) * b;
        tymax = (ymax - py) * b;
    } else {
        tymin = (ymax - py) * b;
        tymax = (ymin - py) * b;
    } 
    if (c >= 0) {
        tzmin = (zmin - pz) * c;
        tzmax = (zmax - pz) * c;
    } else {
        tzmin = (zmax - pz) * c;
        tzmax = (zmin - pz) * c;
    }

    // Obtenció dels temps segons l'eix
    tenterx = std::min(txmin, txmax);
    texitx = std::max(txmin, txmax);
    tentery = std::min(tymin, tymax);
    texity = std::max(tymin, tymax);
    tenterz = std::min(tzmin, tzmax);
    texitz = std::max(tzmin, tzmax);

    // Obtenció dels temps
    tenter = std::max(tenterx, std::max(tentery, tenterz));
    texit = std::min(texitx, std::min(texity, texitz));

    // Si el tenter és molt petit no hi ha intersecció
    if (tenter < powf(10, -4))
        return false;

    vec2 uv(0.0f);
    vec3 hitPoint = r(tenter);

    // Els vectors normals d'una oriented box són (1,0,0), (0,1,0) i (0,0,1)
    // i (-1,0,0), (0,-1,0) i (0,0,-1), en funció de la cara on ens trobem
    if (tenter == txmin) {
        normal = vec3(-1, 0, 0); // Per sobre de la cara amb normal en X
        // Per una cara amb normal en X, projectem sobre el pla YZ  
        uv.x = (hitPoint.z - zmin) / (zmax - zmin);
        uv.y = (hitPoint.y - ymin) / (ymax - ymin);
    }
    else if (tenter == txmax) {
        normal = vec3(1, 0, 0); // Per sota de la cara amb normal en X
        uv.x = (hitPoint.z - zmin) / (zmax - zmin);
        uv.y = (hitPoint.y - ymin) / (ymax - ymin);
    }
    else if (tenter == tymin) {
        normal = vec3(0, -1, 0); // Per sobre de la cara amb normal en Y
        // Per una cara amb normal en Y, projectem sobre el pla XZ
        uv.x = (hitPoint.x - xmin) / (xmax - xmin);
        uv.y = (hitPoint.z - zmin) / (zmax - zmin);
    }
    else if (tenter == tymax) {
        normal = vec3(0, 1, 0); // Per sota de la cara amb normal en Y
        uv.x = (hitPoint.x - xmin) / (xmax - xmin);
        uv.y = (hitPoint.z - zmin) / (zmax - zmin);
    }
    else if (tenter == tzmin) {
        normal = vec3(0, 0, -1); // Per sobre de la cara amb normal en Z
        // Per una cara amb normal en Z (tzmin), projectem sobre el pla XY
        uv.x = (hitPoint.x - xmin) / (xmax - xmin);
        uv.y = (hitPoint.y - ymin) / (ymax - ymin);
    }
    else if (tenter == tzmax) {
        normal = vec3(0, 0, 1); // Per sota de la cara amb normal en Z
        // Per la cara amb normal en z (tzmax) també projectem sobre el pla XY  
        uv.x = (hitPoint.x - xmin) / (xmax - xmin);
        uv.y = (hitPoint.y - ymin) / (ymax - ymin);
    }
    else {
        normal = vec3(0, 0, 0); // No hauria de passar mai
    }

    // Retornem true si hi ha intersecció i omplim el ShadeInfo
    if (tenter < texit)
    {
        shadeInfo.t = tenter;
        shadeInfo.p = r(tenter);
        shadeInfo.normal = normal;
        shadeInfo.mat = material;
        shadeInfo.uv = uv;
        return true;
    }
    return false;
}

bool Box::allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const
{
    return false;
}

void Box::aplicaTG(shared_ptr<TG> t)
{
    if (dynamic_pointer_cast<TranslateTG>(t))
    {
        for (vec3 &vertex : vertexs)
        {
            vec4 v(vertex, 1.0);
            v = t->getTG() * v;
            vertex.x = v.x;
            vertex.y = v.y;
            vertex.z = v.z;
        }
    }
}

void Box::update(int frame)
{
    Animable::update(frame);
}

shared_ptr<Box> Box::getBoundingBox() const
{
    return make_shared<Box>(vertexs);
}
