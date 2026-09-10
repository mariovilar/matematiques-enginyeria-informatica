#pragma once

#include <memory>

#include <glm/glm.hpp>
#include "Camera.hpp"
#include "Utilities/Image.hpp"

using namespace glm;
using namespace std;

class Config
{
public:
    int viewportWidth;
    int viewportHeight;
    vec3 observador;
    vec3 vrp;
    float fov;

    vec3 lightPos;
    vec3 lightAmbient;
    vec3 lightDiffuse;
    vec3 lightSpecular;

    int numRays;
    int numSpheres;
    int numSamples;

    int backgroundMode; // 0 = color, 1 = textura
    vec3 backgroundColor;
    shared_ptr<Image> background;

    int selectedTracer; // 0 = Raycasting, 1 = Raytracing, 2 = Pathtracing
    int selectedShader; // 0 = NormalShading, 1 = DiffuseShading, 2 = BlinnPhongShading, 3 = ToonShading

    bool shadowMode;   // false = no shadows, true = shadows
    bool boundingBox;  // false = no boundingBox, true = boundingBox
    bool depthShading; // false = no depthShading, true = depthShading

    int MAXDEPTH; // Max depth for ray tracing

    Config(int ww, int wh)
    {
        viewportWidth = ww;
        viewportHeight = wh;

        lightPos = vec3(0.0f, 5.0f, 0.0f);
        lightAmbient = vec3(0.1f, 0.1f, 0.1f);
        lightDiffuse = vec3(0.8f, 0.8f, 0.8f);
        lightSpecular = vec3(1.0f, 1.0f, 1.0f);

        numRays = 10;
        numSpheres = 3;
        numSamples = 1;

        observador = vec3(0.0f, 0.0f, 5.0f);
        vrp = vec3(0.0f, 0.0f, -1.0f);
        fov = 90.0f;

        backgroundMode = false;
        backgroundColor = vec3(0.9f, 0.9f, 0.95f);
        background = nullptr;

        selectedTracer = 0;
        selectedShader = 0;

        shadowMode = false;
        boundingBox = false;
        depthShading = false;

        MAXDEPTH = 0;
    }

    // Mètode per fer els testos més ràpidament, si ens convé
    void setParameters(vec3 Pos, vec3 Ambient, vec3 Diffuse, vec3 Specular, vec3 Obs, vec3 Vrp, vec3 BColor, float Fov)
    {
        lightPos = Pos;
        lightAmbient = Ambient;
        lightDiffuse = Diffuse;
        lightSpecular = Specular;
        observador = Obs;
        vrp = Vrp;
        backgroundColor = BColor;
        fov = Fov;
    }

    ~Config()
    {
        if (background)
            background->freeImage();
    }

    Config &operator=(const Config &other)
    {
        if (this != &other)
        { // Evitar autoassignació
            viewportWidth = other.viewportWidth;
            viewportHeight = other.viewportHeight;
            observador = other.observador;
            vrp = other.vrp;
            lightPos = other.lightPos;
            lightAmbient = other.lightAmbient;
            lightDiffuse = other.lightDiffuse;
            lightSpecular = other.lightSpecular;
            numRays = other.numRays;
            numSpheres = other.numSpheres;
            numSamples = other.numSamples;
            backgroundMode = other.backgroundMode;
            backgroundColor = other.backgroundColor;
            background = other.background;
            selectedTracer = other.selectedTracer;
            selectedShader = other.selectedShader;
            shadowMode = other.shadowMode;
            boundingBox = other.boundingBox;
        }
        return *this; // Retornem *this per permetre assignacions encadenades
    }
};
