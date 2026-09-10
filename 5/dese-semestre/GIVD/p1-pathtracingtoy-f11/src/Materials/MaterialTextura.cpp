#include "Materials/MaterialTextura.hpp"

MaterialTextura::MaterialTextura()
{
    // Carrega la imatge de textura utilitzant la classe Image
    texture = std::make_shared<Image>();
}

MaterialTextura::MaterialTextura(const char *filename)
{
    // Carrega la imatge de textura utilitzant la classe Image
    setTexture(filename);
}

void MaterialTextura::setTexture(const char *filename)
{
    // Carrega la imatge de textura utilitzant la classe Image
    texture = std::make_shared<Image>(filename);
}

MaterialTextura::~MaterialTextura()
{
    // Destructor (la shared_ptr s'encarrega de la gestió de la memòria)
}

bool MaterialTextura::evaluate(const Ray &r_in, float t, vec3 normal, vec3 &color, Ray &r_out) const
{
    // No es calculen raigs secundaris; simplement retorna el color difús de la textura
    return false;
}

vec3 MaterialTextura::getDiffuse(vec2 uv) const
{
    return texture->getPixelColor(uv);
}