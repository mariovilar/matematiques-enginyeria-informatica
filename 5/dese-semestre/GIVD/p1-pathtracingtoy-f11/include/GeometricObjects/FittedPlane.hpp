#pragma once

#include "Object.hpp"
#include "Plane.hpp"
#include "Utilities/TGs/TranslateTG.hpp"

#pragma once


class FittedPlane: public Plane {
public:
    FittedPlane() {};
    FittedPlane(vec3 normal, vec3 pass_point, vec2 pmin, vec2 pmax, bool center);
    FittedPlane(vec3 normal, float d, vec2 pmin, vec2 pmax, bool center);

    virtual ~FittedPlane(){}
    virtual bool hit (Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;
    virtual bool allHits(Ray& r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const override;

    virtual void update(int nframe) override;
    virtual void aplicaTG(shared_ptr<TG> tg) override;

    vec2 pmin;
    vec2 pmax;
    bool center;
};
