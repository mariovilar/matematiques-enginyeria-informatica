#pragma once

#include <memory>

#include <glm/glm.hpp>
#include "Lights/GPUPointLight.hpp"
#include "Utilities/Image.hpp"


using namespace glm;
using namespace std;

class GPUConfig {
    public:

      // Viewport settings
      int viewportWidth;
      int viewportHeight;

      // Camera settings
      vec3 observador;
      vec3 vrp;
      vec3 vup;
      float fov;
      float zNear;
      float zFar;

      vector<GPUPointLight> lights;
      vec3 lightAmbientGlobal;
      int   maxLights; // Maximum number of lights allowed
      
      // GPUMaterial properties of the loaded object
      float shininess;
      float ambientSlider;
      float diffusionSlider;
      float specularSlider;
      bool  customColor;
      
      vec3 ambientColor;
      vec3 diffuseColor;
      vec3 specularColor;

      // Rendering options
      int renderMode;  // 0 = Smooth, 1 = Lines, 2 = Point Cloud, 3 = Mesh
      
      // Input controls
      int mouseMode;   // 0 = Translate, 1 = Rotate
      int buttonEnabled; // 0 = Disabled, 1 = Enabled
      float sensitivityAmount;
      
      // Transformation
      float sx, sy;
      float tx, ty;

      // Scene settings
      float zThreshold;
      vec3 backgroundColor;
      

      int selectedShader; // 0 = ColorShading, 1 = MaterialShading, 2 = NormalShading, 3= Gouraud, 4 = Phong, 5 = Toon, 6 = Texture
      int envMap; // 0 = No, 1 = CubMap, 2 = Reflexions, 3 = Transparencies

      GPUConfig(): viewportWidth(800), viewportHeight(600) {
      }

      GPUConfig(int ww, int wh) {
            // Viewport settings
            viewportWidth = ww;
            viewportHeight = wh;
            
            // Camera settings
            observador = vec3(5.0f, 3.0f, 5.0f);
            vrp = vec3(0.0f, 0.0f, 0.0f);
            vup = vec3(0.0f, 1.0f, 0.0f);
            fov = 30.0f;
            zNear = 0.1f;
            zFar = 100.0f;
            
            // Light settings
            maxLights = 8; // Allow up to 8 lights
            
            // Add default light
            lights.push_back(GPUPointLight(
                vec3(0.0f, 5.0f, 0.0f),
                vec3(0.1f, 0.1f, 0.1f),
                vec3(0.8f, 0.8f, 0.8f),
                vec3(1.0f, 1.0f, 1.0f)
            ));
            
            lightAmbientGlobal = vec3(0.05f, 0.05f, 0.05f);
            
            // GPUMaterial properties
            shininess = 10.0f;
            ambientSlider = 0.05f;
            diffusionSlider = 0.2f;
            specularSlider = 0.5f;
            customColor = false;
            ambientColor = vec3(0.05f, 0.05f, 0.05f);
            diffuseColor = vec3(0.8f, 0.2f, 0.2f);
            specularColor = vec3(0.5f, 0.5f, 0.5f);
            
            // Rendering options
            renderMode = 3;  // Default to Mesh rendering
            
            // Input controls
            mouseMode = 1;   // 0 - translate, 1 -  rotate
            buttonEnabled = 0; // 0 - Disabled, 1 - Enabled
            sensitivityAmount = 0.5f;
            
            // Transformation
            sx = sy = 1.0f;
            tx = ty = 0.0f;

            // Scene settings
            backgroundColor = vec3(0.9f, 0.9f, 0.95f);
            zThreshold = 0.0f;

            selectedShader = 0; 
            envMap = 0; //0- No, 1- CubMap, 2- Reflexions, 3- Transparencies
        }
      
      GPUConfig(const GPUConfig& other) {
          viewportWidth = other.viewportWidth;
          viewportHeight = other.viewportHeight;
          observador = other.observador;
          vrp = other.vrp;
          vup = other.vup;
          fov = other.fov;
          zNear = other.zNear;
          zFar = other.zFar;
          // Light settings
          lights = other.lights;
          lightAmbientGlobal = other.lightAmbientGlobal;
          maxLights = other.maxLights;
          
          lightAmbientGlobal = other.lightAmbientGlobal;
          
          shininess = other.shininess;
          ambientSlider = other.ambientSlider;
          diffusionSlider = other.diffusionSlider;
          specularSlider = other.specularSlider;
          customColor = other.customColor;
          ambientColor = other.ambientColor;
          diffuseColor = other.diffuseColor;
          specularColor = other.specularColor;
          renderMode = other.renderMode;
          
          mouseMode = other.mouseMode;
          buttonEnabled = other.buttonEnabled;
         
          sensitivityAmount = other.sensitivityAmount;
          sx = other.sx;
          sy = other.sy;
          tx = other.tx;
          ty = other.ty;
          
          backgroundColor = other.backgroundColor;
          zThreshold = other.zThreshold;
                          
          selectedShader = other.selectedShader;
          envMap = other.envMap;
      }

      ~GPUConfig() {
      }

      GPUConfig& operator=(const GPUConfig& other) {
          if (this != &other) { // Evitar autoassignació
                viewportWidth = other.viewportWidth;
                viewportHeight = other.viewportHeight;
                observador = other.observador;
                vrp = other.vrp;
                vup = other.vup;
                fov = other.fov;
                zNear = other.zNear;
                zFar = other.zFar;
                // Light settings
                lights = other.lights;
                lightAmbientGlobal = other.lightAmbientGlobal;
                maxLights = other.maxLights;
                
                lightAmbientGlobal = other.lightAmbientGlobal;
                
                shininess = other.shininess;
                ambientSlider = other.ambientSlider;
                diffusionSlider = other.diffusionSlider;
                specularSlider = other.specularSlider;
                customColor = other.customColor;
                ambientColor = other.ambientColor;
                diffuseColor = other.diffuseColor;
                specularColor = other.specularColor;
                renderMode = other.renderMode;
                
                mouseMode = other.mouseMode;
                buttonEnabled = other.buttonEnabled;
                
                sensitivityAmount = other.sensitivityAmount;
                sx = other.sx;
                sy = other.sy;
                tx = other.tx;
                ty = other.ty;
                
                backgroundColor = other.backgroundColor;
                zThreshold = other.zThreshold;
                
                selectedShader = other.selectedShader;
                envMap = other.envMap;
          }
          return *this; // Retornem *this per permetre assignacions encadenades
      }

      // Add convenience methods for light management
      void addLight() {
        if (lights.size() < maxLights) {
            lights.push_back(GPUPointLight());
        }
      }
    
      void removeLight(int index) {
        if (index >= 0 && index < lights.size() && lights.size() > 1) {
            lights.erase(lights.begin() + index);
        }
      }
};

