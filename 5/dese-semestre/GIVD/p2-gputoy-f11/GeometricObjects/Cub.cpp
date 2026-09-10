#include "Cub.hpp"

Cub::Cub()
    : a(1), h(1), p(1), xorig(-0.5), yorig(-0.5), zorig(-0.5)
{
    initCub();
}

Cub::Cub(int an, int al, int profu, float x0, float y0, float z0)
    : a(an), h(al), p(profu), xorig(x0), yorig(y0), zorig(z0)
{
    // Initialize vertices and colors
    initCub();

}

void Cub::initCub() {
    vertices[0] = vec4(xorig, yorig, zorig+p, 1.0);
    vertices[1] = vec4(xorig, yorig+h, zorig+p, 1.0);
    vertices[2] = vec4(xorig+a, yorig+h, zorig+p, 1.0);
    vertices[3] = vec4(xorig+a, yorig, zorig+p, 1.0);
    vertices[4] = vec4(xorig, yorig, zorig, 1.0);
    vertices[5] = vec4(xorig, yorig+h, zorig, 1.0);
    vertices[6] = vec4(xorig+a, yorig+h, zorig, 1.0);
    vertices[7] = vec4(xorig+a, yorig, zorig, 1.0);

    vertex_colors[0] = vec4(0.0, 0.0, 0.0, 1.0);
    vertex_colors[1] = vec4(1.0, 0.0, 0.0, 1.0);
    vertex_colors[2] = vec4(1.0, 1.0, 0.0, 1.0);
    vertex_colors[3] = vec4(0.0, 1.0, 0.0, 1.0);
    vertex_colors[4] = vec4(0.0, 0.0, 1.0, 1.0);
    vertex_colors[5] = vec4(1.0, 0.0, 1.0, 1.0);
    vertex_colors[6] = vec4(1.0, 1.0, 1.0, 1.0);
    vertex_colors[7] = vec4(0.0, 1.0, 1.0, 1.0);

}
Cub::~Cub()
{
    // Cleanup if necessary
    if (textureID != 0) {
        glDeleteTextures(1, &textureID);
    }
}

void Cub::make()
{
   // Inicialitzar les estructures 
    objectVertices.clear();
    objectColors.clear();
    objectTextures.clear();

    quad(1, 0, 3, 2);
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, 1.0, 0.0));
    quad(2, 3, 7, 6);
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(1.0, 0.0, 0.0, 0.0));
    quad(3, 0, 4, 7);
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, -1.0, 0.0, 0.0));
    quad(6, 5, 1, 2);
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    objectNormals.push_back(vec4(0.0, 1.0, 0.0, 0.0));
    quad(4, 5, 6, 7);
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    objectNormals.push_back(vec4(0.0, 0.0, -1.0, 0.0));
    quad(5, 4, 0, 1);
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    objectNormals.push_back(vec4(-1.0, 0.0, 0.0, 0.0));
    
    vec3 Ka = vec3(0.1f, 0.2f, 0.2f);
    vec3 Kd = vec3(0.4f, 0.7f, 0.9f);
    vec3 Ks = vec3(0.3f, 0.3f, 0.3f);
    float shininess = 150.0f;
    setMaterial(make_shared<GPUMaterial>(Ka, Kd, Ks, shininess));
}

void Cub::quad( int a, int b, int c, int d )
{
    // First triangle: a, b, c
    objectVertices.push_back(vertices[a]);
    objectColors.push_back(vertex_colors[a]);
    objectTextures.push_back(vec2(0.0, 0.0));
    
    objectVertices.push_back(vertices[b]);
    objectColors.push_back(vertex_colors[b]);
    objectTextures.push_back(vec2(0.0, 1.0));
    
    objectVertices.push_back(vertices[c]);
    objectColors.push_back(vertex_colors[c]);
    objectTextures.push_back(vec2(1.0, 1.0));
    
    // Second triangle: a, c, d
    objectVertices.push_back(vertices[a]);
    objectColors.push_back(vertex_colors[a]);
    objectTextures.push_back(vec2(0.0, 0.0));
    
    objectVertices.push_back(vertices[c]);
    objectColors.push_back(vertex_colors[c]);
    objectTextures.push_back(vec2(1.0, 1.0));
    
    objectVertices.push_back(vertices[d]);
    objectColors.push_back(vertex_colors[d]);
    objectTextures.push_back(vec2(1.0, 0.0));
}



