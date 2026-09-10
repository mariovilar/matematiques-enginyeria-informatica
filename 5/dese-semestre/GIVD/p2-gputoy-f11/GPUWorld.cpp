#include "GPUWorld.hpp"

GPUWorld::GPUWorld() {
    camera = make_shared<GPUCamera>(800,600);
    scene = make_shared<GPUScene>();
}

void GPUWorld::toGPU(GLuint program) {
    scene->toGPU(program);
    camera->toGPU(program);
    lightsManager->toGPU(program);
}

void GPUWorld::toGPUTexture(GLuint program) {
    scene->toGPUTexture(program);
    camera->toGPU(program);
    lightsManager->toGPU(program);
}

void GPUWorld::refreshProperties() {
   // Refresc del World amb els valors del setup
   lightsManager->setGlobalAmbientLight(setup->lightAmbientGlobal);
   for (auto& l : setup->lights) {
       lightsManager->addLight(make_shared<GPUPointLight>(l));
   }
    camera->updateCamera(setup->observador, 
                setup->vrp, 
                setup->vup, 
                setup->fov, setup->zNear, setup->zFar);
       
    scene->setMaterial(make_shared<GPUMaterial>(setup->ambientColor, setup->diffuseColor, setup->specularColor, setup->shininess));
}

void GPUWorld::setTGLastObject(glm::mat4 m) {
    scene->setTGLastObject(m);
}

void GPUWorld::aplicaTG(glm::mat4 m) {
    scene->aplicaTG(m);
}