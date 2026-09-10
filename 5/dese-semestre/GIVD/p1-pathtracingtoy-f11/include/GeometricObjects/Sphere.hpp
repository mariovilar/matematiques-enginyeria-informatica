/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
#pragma once

#include "Object.hpp"
#include "GeometricObjects/Box.hpp"
#include "Materials/Material.hpp"
#include "Utilities/TGs/TG.hpp"
#include "Utilities/TGs/TranslateTG.hpp"

class Sphere : public Object
{
public:
    Sphere();

    Sphere(vec3 cen, float r);
    Sphere(vec3 cen, float r, vec3 col);

    Sphere(vec3 cen, float r, shared_ptr<Material> material);

    virtual ~Sphere() {}
    virtual bool hit(Ray &r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;
    virtual bool allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const override;

    virtual void update(int nframe) override;
    virtual void aplicaTG(shared_ptr<TG> tg) override;

    // Compute the bounding box of the object
    virtual shared_ptr<Box> getBoundingBox() const override;

    vec3 getCenter() { return center; };
    float getRadius() { return radius; }

private:
    // Centre de l'esfera
    vec3 center;
    // Radi de l'esfera
    float radius;
};
