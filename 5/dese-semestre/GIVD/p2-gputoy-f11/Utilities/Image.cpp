#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "Utilities/Image.hpp"

Image::Image(const char* filename) { 
    data = stbi_load(filename, &width, &height, &channels, 0);
    if (!data) {
        std::cerr << "Error carregant la imatge: " << filename << std::endl;
        width = height = channels = 0;
        data = nullptr;
    }
}

void Image::saveImage(const char* filename) {
    if (!data) return;  // No fa res si no hi ha imatge
    // Guarda la imatge en format PNG
    
    unsigned char* flippedData = new unsigned char[width * height * channels];
    
    //  Invertir les files de la imatge
    for (int y = 0; y < height; y++) {
         memcpy(
             flippedData + (height - 1 - y) * width * channels,  // Destí (invertit)
             (unsigned char*)data + y * width * channels,        // Origen (original)
             width * channels
        );
    }
    
    //  Guardar la imatge corregida
    stbi_write_png(filename, width, height, channels, flippedData, width * channels);
    
    //  Alliberar memòria
    delete[] flippedData;
}   


void Image::resize(int w, int h) {
    if (data) stbi_image_free(data);
    width = w;
    height = h;
    data = new unsigned char[width * height * channels];
}

void Image::freeImage() {
    if (data) stbi_image_free(data);
    data = nullptr;
    width = height = channels = 0;
}

Image::~Image() {
    if (data) stbi_image_free(data);
}

glm::vec3 Image::getPixelColor(int x, int y) {
    if (!data) return glm::vec3(0.0f);  // Retorna negre si no hi ha imatge

    x = glm::clamp(x, 0, width - 1);
    y = glm::clamp(height-y, 0, height - 1);

    int index = (y * width + x) * channels;
    float r = data[index] / 255.0f;
    float g = data[index + 1] / 255.0f;
    float b = data[index + 2] / 255.0f;

    return glm::vec3(r, g, b);  // Retorna el color normalitzat (0.0 - 1.0)
}
void Image::setPixelColor(int x, int y, glm::vec3 color) {
    if (!data) return;  // No fa res si no hi ha imatge

    x =  glm::clamp(x, 0, width - 1);
    y =  glm::clamp(height-y, 0, height - 1);

    int index = (y * width + x) * channels;
    data[index] = (unsigned char)(color.r * 255.0f);
    data[index + 1] = (unsigned char)(color.g * 255.0f);
    data[index + 2] = (unsigned char)(color.b * 255.0f);
}

glm::vec3 Image::getPixelColor(vec2 uv) {
    if (!data) return glm::vec3(0.0f);  // Retorna negre si no hi ha imatge

    int x, y;

    x = uv.x * width;
    y = (1.0f-uv.y) * height;
    int index = (y * width + x) * channels;
    float r = data[index] / 255.0f;
    float g = data[index + 1] / 255.0f;
    float b = data[index + 2] / 255.0f;

    return glm::vec3(r, g, b);  // Retorna el color normalitzat (0.0 - 1.0)
}
