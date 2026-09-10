#pragma once

#include <vector>
#include <memory>

#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Mesh.hpp"
#include "GeometricObjects/Cub.hpp"
#include "GeometricObjects/GPUMaterial.hpp"

using namespace std;

class GPUScene : public GPUConnectable
{
public:

    GPUScene();
    virtual ~GPUScene();

    // Vector d'objectes continguts a l'escena
    std::vector<shared_ptr<Object>> objects;
    std::vector<shared_ptr<Object>> getObjects() {return objects;}
    void addObject(shared_ptr<Object> obj);

    void setMaterial(shared_ptr<GPUMaterial> m);
    void lastObjectToGPU(GLuint program);
    void lastObjectToGPUTexture(GLuint program);
    
    void toGPU (GLuint p) override;
    void draw() override;  

    void toGPUTexture(GLuint p);
    void updateZThreshold(GLuint program, float zThreshold);

    bool hasObjects() { return objects.size() > 0; };

    bool isTextured() { 
        for (auto obj : objects) {
            if (obj->isTextured()) return true;
        }
        return false;
    };

    void rebindTexture() {
        for (auto obj : objects) {
            if (obj->isTextured()) obj->rebindTexture();
        }
    };

    void initTextureGL(const char *nomTextura) {
        if (objects.empty()) return;
    
        // Per poder tenir textures diferents per cada objecte, ens hem de quedar amb l'últim
        shared_ptr<Object> lastObject = objects.back();
    
        if (!lastObject->isTextured()) {
            lastObject->generateTextureCoordinates();
        }
    
        lastObject->initTextureGL(nomTextura);
    }
    
    void aplicaTG(glm::mat4 m)
    {
        GLuint modelMatrixLoc = glGetUniformLocation(program, "modelMatrix");
        glUniformMatrix4fv(modelMatrixLoc, 1, GL_FALSE, glm::value_ptr(m));
    }; 

    void setTGLastObject(glm::mat4 m) {
        if (objects.size() > 0) {
            objects.at(objects.size()-1)->setTransform(m);
        }
    };

    void updateAdvancedSettings(int setting){
        for (auto obj : objects) {
            // TO DO
        }
    }
};