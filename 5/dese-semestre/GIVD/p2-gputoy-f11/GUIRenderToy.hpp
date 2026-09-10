#pragma once 

#include "GLWidget.hpp"
#include "ImGuiFileDialog/ImGuiFileDialog.h"

class GUIRenderToy {
    
public:
    GUIRenderToy();
    ~GUIRenderToy();

    // ImGui setup i cleanup
    void setup_imgui(GLFWwindow* window);
    void cleanup_imgui();

    // Render ImGui
    void render_imgui(GLWidget& glWidget);

    // Configuració de la GUI
    void    setConfig(GPUConfig& config) { setup = &config; }
    GPUConfig *getConfig() { return setup; }

private:

    bool fileObj = false;
    bool fileTexture = false;
    GPUConfig *setup;
    std::string selectedShading;
    std::string envMap;

    // Components de la GUI
    void renderMenus(GLWidget& glWidget);
    void renderControls(GLWidget& glWidget);

    void openFileDialog(const char *extension);
    void handleFileDialogResult(GLWidget& glWidget);

    bool renderColorPicker(const char* label, glm::vec3* color);
   
};
