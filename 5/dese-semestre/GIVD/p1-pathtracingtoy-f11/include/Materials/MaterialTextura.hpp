#pragma once

#include "Materials/Material.hpp"
#include "Utilities/Image.hpp"
class MaterialTextura : public Material
{
public:
    // Constructor que carrega la imatge de textura
    MaterialTextura();
    MaterialTextura(const char *filename);
    virtual ~MaterialTextura();

    void setTexture(const char *filename);
    virtual bool evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const override;
    virtual vec3 getDiffuse(vec2 uv) const override;

private:
    std::shared_ptr<Image> texture;
};
