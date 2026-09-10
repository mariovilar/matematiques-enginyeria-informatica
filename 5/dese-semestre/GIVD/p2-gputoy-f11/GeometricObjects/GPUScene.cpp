#include "GPUScene.hpp"

GPUScene::GPUScene()
{
    objects.clear();
   
}
/**
 * @brief Scene::~Scene
 */
GPUScene::~GPUScene() {
    objects.clear();
}

/**
 * @brief GPUScene::addObject
 * @param obj
 */
void GPUScene::addObject(shared_ptr<Object> obj) {
    objects.push_back(obj);
}

/**
 * @brief GPUScene::toGPU
 */
void GPUScene::toGPU(GLuint p) {
    program = p;
    for(unsigned int i=0; i < objects.size(); i++){
        objects.at(i)->toGPU(p);
    }
}

void GPUScene::toGPUTexture(GLuint p) {
    this->program = p;
    for (unsigned int i = 0; i < objects.size(); i++) {
        // En cas de no estar texturitzat, generem coordenades de textura
        if (!objects.at(i)->isTextured()) {
            objects.at(i)->generateTextureCoordinates();
        }
        objects.at(i)->toGPUTexture(program);
    }
}

void GPUScene::lastObjectToGPU(GLuint program) {
    if (objects.size()>0) {
        shared_ptr<Object> lastObject = objects.at(objects.size()-1);
        if(lastObject->isTextured()) {
            lastObject->toGPUTexture(program);
        } else {
            lastObject->toGPU(program);
        }
    }
}

void GPUScene::updateZThreshold(GLuint program, float zThreshold) {
    this->program = program;
    GLuint zThresholdLoc = glGetUniformLocation(program, "zThreshold");
    if (zThresholdLoc == -1) {
        // std::cerr << "Error: zThreshold uniform not found in shader program." << std::endl;
        return;
    }
    glUniform1f(zThresholdLoc, zThreshold);
}

/**
 * @brief GPUScene::draw
 */
void GPUScene::draw() {
    for(unsigned int i=0; i < objects.size(); i++) {
        // Apliquem la transformació de l'objecte abans de dibuixar-lo
        aplicaTG(objects.at(i)->getTransform());
        // Draw the object
        objects.at(i)->draw();
    }
}

void GPUScene::setMaterial(shared_ptr<GPUMaterial> m) {
    // Modify the material to the last added object
    if (objects.size()>0) {
        shared_ptr<Object> lastObject = objects.at(objects.size()-1);
        lastObject->setMaterial(m);
    }
}
