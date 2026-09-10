#pragma once

#include <iostream>
#include <memory>

#include <GL/glew.h>

#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <vector>
#include "imgui.h"
#include "backends/imgui_impl_glfw.h"
#include "backends/imgui_impl_opengl3.h"

#include "external/ImGuiFileDialog/stb/stb_image.h"

#include "GPUMaterial.hpp"
#include "Utilities/GPUConnectable.hpp"

using namespace std;
// Es la classe pare de tots els objectes que s'han de visualitzar.
// Es fa una classe Objectes que hereda de Hitable i ha d'implementar el metode intersection

class Object : public GPUConnectable
{
public:
  Object();
  virtual ~Object() {};

  void setMaterial(shared_ptr<GPUMaterial> m);
  shared_ptr<GPUMaterial> getMaterial();
  void materiaToGPU();

  virtual void toGPU(GLuint program) override;
  void draw() override;

  void initTextureGL(const char *nomTextura);
  void rebindTexture();

  void setTransform(glm::mat4 t) { transform = t; };
  glm::mat4 getTransform() const { return transform; };

  bool isTextured() { return objectTextures.size() != 0; };
  void toGPUTexture(GLuint program);
  void generateTextureCoordinates();

protected:
  shared_ptr<GPUMaterial> material;       // Material de l'objecte
  glm::mat4 transform = glm::mat4(1.f);   // Com que aplicaTG s'està aplicant una única matriu que afecta tot el que 
                                          // es dibuixi amb el shader actiu, cada objecte (Object, Mesh, Cub, etc.) 
                                          // necessita tenir la seva pròpia variable membre per guardar la seva matriu de transformació
  std::vector<glm::vec4> objectVertices;
  std::vector<glm::vec4> objectColors;
  std::vector<glm::vec4> objectNormals;
  std::vector<glm::vec2> objectTextures;

  GLuint VAO;
  GLuint vertex_buffer, texture_buffer, color_buffer, normal_buffer;

  GLuint textureID;
};