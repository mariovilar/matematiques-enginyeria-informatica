#pragma once

#include "Object.hpp"
#include "Utilities/TGs/TranslateTG.hpp"


class Plane: public Object {
public:
    Plane() {};
    Plane(vec3 normal, vec3 pass_point);
    Plane(vec3 normal, float d);

    virtual ~Plane(){}
    
    virtual bool hit (Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;
    virtual bool allHits(Ray& r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const override;

    virtual void update(int nframe) override;
    virtual void aplicaTG(shared_ptr<TG> tg) override;

    vec3 normal;
    vec3 point;

};
