#pragma once

#include "external/ImGuiFileDialog/stb/stb_image.h"
#include "external/ImGuiFileDialog/stb/stb_image_write.h"
#include <iostream>
#include <stdlib.h>

#include <glm/glm.hpp>

using namespace std;
using namespace glm;

class Image {
public:

    Image() : data(nullptr), width(0), height(0), channels(0) {};
    Image(int w, int h, int c) : data(new unsigned char[w * h * c]), width(w), height(h), channels(c) {};
    // Funció per carregar la imatge
    Image(const char* filename); 

    unsigned char * getData() {
        return data;
    };

    // Funció per obtenir el color d'un píxel (x, y)
    glm::vec3 getPixelColor(int x, int y);

    // Funció per obtenir el color d'un píxel amb coordenades de textura (u, v)
    glm::vec3 getPixelColor(vec2 uv);

    // Funció per posar el color d'un píxel (x, y)    
    void setPixelColor(int x, int y, glm::vec3 color);

    void saveImage(const char* filename);

    void resize(int w, int h);

    void freeImage();

    ~Image();

    private:
        unsigned char* data;  // Punter als píxels de la imatge
        int width, height, channels;
};
