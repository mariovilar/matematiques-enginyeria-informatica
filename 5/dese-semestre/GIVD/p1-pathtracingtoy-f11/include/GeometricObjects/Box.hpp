#pragma once

#include <vector>

#include "Object.hpp"
#include "Utilities/TGs/TG.hpp"
#include "Utilities/TGs/TranslateTG.hpp"

using namespace std;

class Box : public Object
{
public:
    Box();
    Box(vector<vec3> vertexs);
    Box(vector<vec3> vertexs, shared_ptr<Material> material);

    virtual bool hit(Ray &r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;
    virtual bool allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const override;

    virtual void update(int nframe) override;
    virtual void aplicaTG(shared_ptr<TG> tg) override;

    vector<vec3> getVertexs() { return vertexs; }
    virtual ~Box();

    // Compute the bounding box of the object
    virtual shared_ptr<Box> getBoundingBox() const override;

private:
    vector<vec3> vertexs; // vertexs de l'objecte sense repetits
};
