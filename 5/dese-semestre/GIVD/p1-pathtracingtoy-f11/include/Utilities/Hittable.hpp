#pragma once

#include "Utilities/Ray.hpp"
#include "Utilities/ShadeInfo.hpp"

class Hittable {
public:
    Hittable(){};
    ~Hittable() {};

    // Funcio que retorna la intersecció més propera al t_min del raig. La intersecció
    // estarà entre t_min i t_max
    virtual bool hit (Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const = 0;

    // Mètode que retorna totes les interseccions que es troben al llarg del raig entre tmin i tmax
    virtual bool allHits(Ray& r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const = 0;
};
