#include "GUIRenderToy.hpp"

GUIRenderToy::GUIRenderToy()
    : fileObj(false), fileTexture(false), setup(nullptr), selectedShading("Color"), envMap("CubeMap") {
}

GUIRenderToy::~GUIRenderToy() {
}

void GUIRenderToy::setup_imgui(GLFWwindow* window) {
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO(); (void)io;

    // Setup Dear ImGui style
    ImGui::StyleColorsDark();

    ImGui_ImplGlfw_InitForOpenGL(window, true);
    ImGui_ImplOpenGL3_Init("#version 330");
}

void GUIRenderToy::cleanup_imgui()
{
    ImGui_ImplOpenGL3_Shutdown();
    ImGui_ImplGlfw_Shutdown();
    ImGui::DestroyContext();
}

void GUIRenderToy::render_imgui(GLWidget& glWidget)
{   
    // Cal que s'hagi cridat setConfig() abans de render_imgui()
    if (!setup) {
        ImGui::Begin("Error");
        ImGui::Text("ERROR: Config sense inicialitzar. Cal cridar abans de render_imgui a setConfig() .");
        return;
    }

    // Render menus (main menu bar)
    renderMenus(glWidget);
    
    // Render control panel
    renderControls(glWidget);
    
    // Handle file dialog if open
    handleFileDialogResult(glWidget);
}

void GUIRenderToy::renderMenus(GLWidget& glWidget)
{
    setup = &(glWidget.getConfig());
    // Main menu bar
    if (ImGui::BeginMainMenuBar()) {
        if (ImGui::BeginMenu("Files")) {
            if (ImGui::MenuItem("Create Cub ....")) {
                //Creació d'un cub centrat al 0,0,0 amb mida 1
                glWidget.addCube();
            }
            if (ImGui::MenuItem("Open Mesh....")) {
                // Aquí pots guardar la renderització
                fileObj = true;
                openFileDialog(".obj");
            }
            if (ImGui::MenuItem("Reset Scene")) {
                // Aquí pots guardar la renderització
                glWidget.reset();
            }   

            ImGui::EndMenu();
        }

        // Shadings menu
        if (ImGui::BeginMenu("Shadings")) {
            // Color shader option
            if (ImGui::MenuItem("Color", NULL, selectedShading == "Color")) {
                selectedShading = "Color";
                setup->selectedShader = 0;
                glWidget.activateShader("Color", NULL);
            }
            // Color shader option
            if (ImGui::MenuItem("Material", NULL, selectedShading == "Material")) {
                selectedShading = "Material";
                setup->selectedShader = 1;
                glWidget.activateShader("Material", NULL);
            }// Color shader option
            if (ImGui::MenuItem("Normal", NULL, selectedShading == "Normal")) {
                selectedShading = "Normal";
                setup->selectedShader = 2;
                glWidget.activateShader("Normal", NULL);
            }
            // Color shader option
            if (ImGui::MenuItem("Gouraud", NULL, selectedShading == "Gouraud")) {
                selectedShading = "Gouraud";
                setup->selectedShader = 3;
                glWidget.activateShader("Gouraud", NULL);
            }
            // Color shader option
            if (ImGui::MenuItem("Phong", NULL, selectedShading == "Phong")) {
                selectedShading = "Phong";
                setup->selectedShader = 4;
                glWidget.activateShader("Phong", NULL);
            }
            // Color shader option
            if (ImGui::MenuItem("Toon", NULL, selectedShading == "Toon")) {
                selectedShading = "Toon";
                setup->selectedShader = 5;
                glWidget.activateShader("Toon", NULL);
            }

            // Texture shader option
            if (ImGui::MenuItem("Texture", NULL, selectedShading == "Texture")) {
                selectedShading = "Texture";
                setup->selectedShader = 6;
                fileTexture = true;
                openFileDialog(".png,.jpg,.jpeg,.tga,.bmp");
            }

            // Texture shader option
            if (ImGui::MenuItem("Phong (Texture)", NULL, selectedShading == "PhonTex")) {
                selectedShading = "PhonTex";
                setup->selectedShader = 7;
                fileTexture = true;
                openFileDialog(".png,.jpg,.jpeg,.tga,.bmp");
            }

            // Re-load shaders
            if (ImGui::MenuItem("Reload Shaders")) {
                // Ara si el shading seleccionat és Texture no es deixa activat per que no 
                // es pot carregar sense un fitxer de textura. 
                if (setup->selectedShader == 6 || setup->selectedShader == 7) {
                    setup->selectedShader = 0;
                    selectedShading = "Color";
                }
                glWidget.initShadersGPU();
                glWidget.activateShader(selectedShading.c_str(), NULL);
            }
            
            ImGui::EndMenu();
        }
        if (ImGui::BeginMenu("Advanced")) {
            // Color shader option
            if (ImGui::MenuItem("Environmental Mapping", NULL, envMap == "CubeMap")) {
                envMap = "CubeMap";
                setup->envMap = 1;
                glWidget.updateAdvancedSettings();
            }

            if (ImGui::MenuItem("Reflections", NULL, envMap == "Reflections")) {
                envMap = "Reflections";
                setup->envMap = 2;
                
            }
            if (ImGui::MenuItem("Transparencies", NULL, envMap == "Transparencies")) {
                envMap = "Transparencies";
                setup->envMap = 3;
                
            }
            ImGui::EndMenu();
        }
        ImGui::EndMainMenuBar();
    }
}

void GUIRenderToy::renderControls(GLWidget& glWidget) {
    // Control panel window
    ImGui::Begin("Configuration");

    // Display framerate
    ImGui::Text("Application %.3f ms/frame (%.1f FPS)", 
                1000.0f / ImGui::GetIO().Framerate, 
                ImGui::GetIO().Framerate);
    ImGui::Text(" ");

    // Render Mode Section
    ImGui::Separator(); 
    ImGui::TextColored(ImVec4(0.0f, 1.0f, 1.0f, 1.0f), "Render Mode"); 
    ImGui::Separator();
    
    int renderMode = glWidget.getConfig().renderMode;
    if (ImGui::RadioButton("Smooth", &renderMode, 0)) { glWidget.setRenderMode(renderMode); }
    ImGui::SameLine();
    if (ImGui::RadioButton("Lines", &renderMode, 1)) { glWidget.setRenderMode(renderMode); }
    ImGui::SameLine();
    if (ImGui::RadioButton("Point Cloud", &renderMode, 2)) { glWidget.setRenderMode(renderMode); }
    ImGui::SameLine();
    if (ImGui::RadioButton("Mesh", &renderMode, 3)) { glWidget.setRenderMode(renderMode); }
    
    // GPUMaterial Properties Section
    ImGui::Separator(); 
    ImGui::TextColored(ImVec4(0.0f, 1.0f, 1.0f, 1.0f), "GPUMaterial de l'objecte"); 
    ImGui::Separator();
    ImGui::Text("Aquests colors i propietats permeten modificar com l'objecte interacciona amb la llum,");
    ImGui::Text("donant la impressió d'estar fet de diferents materials.");
    ImGui::Separator();
    
    // Get current config values
    GPUConfig& setup = glWidget.getConfig();

    bool customColor = setup.customColor;
    float ambientSlider = setup.ambientSlider;
    float diffusionSlider = setup.diffusionSlider;
    float specularSlider = setup.specularSlider;
    float shininess = setup.shininess;
    vec3 ambientColor = setup.ambientColor;
    vec3 diffuseColor = setup.diffuseColor;
    vec3 specularColor = setup.specularColor;
    
    // GPUMaterial controls
    if (ImGui::Checkbox("Custom Materials", &customColor)) {
        setup.customColor = customColor;
    }
    
    if (customColor) {
        // Ús de color pickers per controlar els colors
        ImGui::Text("GPUMaterial Colors:");
        
        // Ambient color picker
        if (renderColorPicker("Ambient", &setup.ambientColor)) {
            glWidget.updateMaterial();
        }
        
        // Diffuse color picker
        if (renderColorPicker("Diffuse", &setup.diffuseColor)) {
            glWidget.updateMaterial();
        }
        
        // Specular color picker
        if (renderColorPicker("Specular", &setup.specularColor)) {
            glWidget.updateMaterial();
        }
        
        if (ImGui::SliderFloat("Shininess", &shininess, 0.0f, 150.0f)) {
            setup.shininess = shininess;
            glWidget.updateMaterial();
        }
    } else {
        // Us de sliders per controlar els colors
        if (ImGui::SliderFloat("Ambient", &ambientSlider, 0.0f, 1.0f)) {
            setup.ambientSlider = ambientSlider;
            setup.ambientColor = vec3(ambientSlider, ambientSlider, ambientSlider);
            glWidget.updateMaterial();
        }
        
        if (ImGui::SliderFloat("Diffusion", &diffusionSlider, 0.0f, 1.0f)) {
            setup.diffusionSlider = diffusionSlider;
            setup.diffuseColor = vec3(diffusionSlider, diffusionSlider, diffusionSlider);
            glWidget.updateMaterial();
        }
        
        if (ImGui::SliderFloat("Specular", &specularSlider, 0.0f, 1.0f)) {
            setup.specularSlider = specularSlider;
            setup.specularColor = vec3(specularSlider, specularSlider, specularSlider);
            glWidget.updateMaterial();
        }
        
        if (ImGui::SliderFloat("Shininess", &shininess, 0.0f, 150.0f)) {
            setup.shininess = shininess;
            glWidget.updateMaterial();
        }
    }
    ImGui::Text(" ");
    
    // Input Devices Section
    ImGui::Separator(); 
    ImGui::TextColored(ImVec4(0.0f, 1.0f, 1.0f, 1.0f), "Mouse Device"); 
    ImGui::Separator();
    ImGui::Text("Selecció la transformació de l'objecte (translació o rotació).");
    ImGui::Text("Per defecte, el ratolí controla la rotació.");
    ImGui::Separator();
    
    int mouseMode = setup.mouseMode;
    float sensitivityAmount = setup.sensitivityAmount;
    bool buttonEnabled = setup.buttonEnabled;
    
    ImGui::Text("Use mouse to "); ImGui::SameLine();
    if (ImGui::RadioButton("translate", &mouseMode, 0)) {
        setup.mouseMode = mouseMode;
    }
    ImGui::SameLine();
    if (ImGui::RadioButton("rotate", &mouseMode, 1)) {
        setup.mouseMode = mouseMode;
    }
    
    
    if (ImGui::SliderFloat("Sensitivity", &sensitivityAmount, 0.1f, 5.0f)) {
        setup.sensitivityAmount = sensitivityAmount;
    }

    if (ImGui::Checkbox("Enabled (prova)", &buttonEnabled)) {
        setup.buttonEnabled = buttonEnabled;
        glWidget.buttonPressEvent();
    }
    
    
    // Camera Section
    ImGui::Separator(); 
    ImGui::TextColored(ImVec4(0.0f, 1.0f, 1.0f, 1.0f), "Camera"); 
    ImGui::Separator();
    
    float fov = setup.fov;
    float zNear = setup.zNear;
    float zFar = setup.zFar;
    float camPos[3] = {setup.observador.x, setup.observador.y, setup.observador.z};
    float vrpPos[3] = {setup.vrp.x, setup.vrp.y, setup.vrp.z};

    if (ImGui::SliderFloat("Field of view", &fov, 0.0f, 180.0f)) {
        setup.fov = fov;
        glWidget.updateCamera();
    }
    
    if (ImGui::SliderFloat("Near plane", &zNear, 0.01f, 10.0f)) {
        setup.zNear = zNear;
        glWidget.updateCamera();
    }
    
    if (ImGui::SliderFloat("Far plane", &zFar, 10.0f, 1000.0f)) {
        setup.zFar = zFar;
        glWidget.updateCamera();
    }
    
    if (ImGui::SliderFloat3("Observador", camPos, -20.0f, 20.0f)) {
        setup.observador = vec3(camPos[0], camPos[1], camPos[2]);
        glWidget.updateCamera();
    }
    
    if (ImGui::SliderFloat3("VRP", vrpPos, -20.0f, 20.0f)) {
        setup.vrp = vec3(vrpPos[0], vrpPos[1], vrpPos[2]);
        glWidget.updateCamera();
    }

    // Lighting Section
    ImGui::Separator(); 
    ImGui::TextColored(ImVec4(0.0f, 1.0f, 1.0f, 1.0f), "Lighting"); 
    ImGui::Separator();
    ImGui::Text("Configure scene lighting");

    float zThreshold = setup.zThreshold;
    if (ImGui::SliderFloat("Z Threshold", &zThreshold, -15.0f, 15.0f)) {
        setup.zThreshold = zThreshold;
        glWidget.updateZThreshold();
    }

    // Global ambient settings
    ImGui::Text("Global Ambient Light:");
    if (renderColorPicker("Global Ambient", &setup.lightAmbientGlobal)) {
        glWidget.updateGlobalAmbientLight();
    }

    // Light management buttons
    if (ImGui::Button("Add Light") && setup.lights.size() < setup.maxLights) {
        setup.addLight();
        //glWidget.updateSingleLight(setup.lights.size()-1);
        glWidget.updateAllLights();
    }

    ImGui::SameLine();

    if (ImGui::Button("Remove Last Light") && setup.lights.size() > 1) {
        setup.removeLight(setup.lights.size() - 1);
        glWidget.updateAllLights();
    }

    // Display number of lights
    ImGui::Text("Lights: %zu/%d", setup.lights.size(), setup.maxLights);

    // Individual light controls
    for (int i = 0; i < setup.lights.size(); i++) {
        // Create a collapsible section for each light
        std::string lightLabel = "Light " + std::to_string(i + 1);
        if (ImGui::CollapsingHeader(lightLabel.c_str())) {
            // Add a unique ID for each control to avoid conflicts
            ImGui::PushID(i);
            
            // Enable/disable light
            bool enabled = setup.lights[i].isEnabled();
            if (ImGui::Checkbox("Enabled", &enabled)) {
                setup.lights[i].setEnabled(enabled);
                glWidget.updateAllLights();
            }
            
            if (!enabled) {
                ImGui::PopID();
                continue;
            }
            // Light position - keep as sliders
            float position[3] = {
                setup.lights[i].getPos().x,
                setup.lights[i].getPos().y,
                setup.lights[i].getPos().z
            };
            
            if (ImGui::SliderFloat3("Position", position, -25.0f, 25.0f)) {
                setup.lights[i].setPos(vec3(position[0], position[1], position[2]));
                glWidget.updateAllLights();
//                glWidget.updateSingleLight(i);
            }
            
            // Light colors - use color pickers
            ImGui::Text("Light Colors:");
            
            // Create unique labels for each light's color pickers
            std::string ambientLabel = "Ambient##Light" + std::to_string(i);
            std::string diffuseLabel = "Diffuse##Light" + std::to_string(i);
            std::string specularLabel = "Specular##Light" + std::to_string(i);
            
            // Ambient color picker
            vec3 Ia = setup.lights[i].getIa();
            if (renderColorPicker(ambientLabel.c_str(), &Ia)) {
                setup.lights[i].setIa(Ia);
                glWidget.updateAllLights();
               // glWidget.updateSingleLight(i);
            }
            
            // Diffuse color picker
            vec3 Id = setup.lights[i].getId();
            if (renderColorPicker(diffuseLabel.c_str(), &Id)) {
                setup.lights[i].setId(Id);
                glWidget.updateAllLights();
//                glWidget.updateSingleLight(i);
            }
            
            
            // Specular color picker
            vec3 Is = setup.lights[i].getIs();
            if (renderColorPicker(specularLabel.c_str(), &Is)) {
                setup.lights[i].setIs(Is);
                glWidget.updateAllLights();
//                glWidget.updateSingleLight(i);
            }
            
            
            // Remove this specific light button
            if (setup.lights.size() > 1 && ImGui::Button("Remove This Light")) {
                setup.removeLight(i);
                glWidget.updateAllLights();
                ImGui::PopID();
                break; // Break to avoid issues with the removed element
            }
            
            ImGui::PopID();
        }
    }   
    //  SECCIÓ FONS
    if (ImGui::CollapsingHeader("Fons", ImGuiTreeNodeFlags_DefaultOpen)) {
        ImGui::Separator();
        
        ImGui::Text("Selecció de Fons:"); ImGui::SameLine();
        
        if (renderColorPicker("Color de Fons", &setup.backgroundColor)) {
            glWidget.updateBackground();
        }
    }

}

void GUIRenderToy::openFileDialog(const char *extension) {
    // Configure file dialog
    IGFD::FileDialogConfig config;
    config.path = "./";  // Initial path
    config.flags = ImGuiFileDialogFlags_Modal;
    config.sidePaneWidth = 800; // Dialog size

    // Open file dialog
    ImGuiFileDialog::Instance()->OpenDialog(
        "ChooseFileDlgKey", 
        "Select a File", 
        extension, 
        config
    );
}

void GUIRenderToy::handleFileDialogResult(GLWidget& glWidget) {
    // Check if file dialog is open
    if (ImGuiFileDialog::Instance()->Display("ChooseFileDlgKey")) {
        // If OK was clicked and a file was selected
        if (ImGuiFileDialog::Instance()->IsOk()) {
            // Get the selected file path
            std::string selectedFile = ImGuiFileDialog::Instance()->GetFilePathName();
            
            if (fileObj) {
                // Load the selected OBJ file
                glWidget.loadObject(selectedFile.c_str());
                fileObj = false;
            } else {
                if (fileTexture) {
                    // Load the selected texture file
                    if(setup->selectedShader == 6) {
                        glWidget.activateShader("Texture", selectedFile.c_str());
                    } else if (setup->selectedShader == 7) {
                        glWidget.activateShader("PhonTex", selectedFile.c_str());
                    }
                    fileTexture = false;
                }
            }
        }
        
        // Close the dialog
        ImGuiFileDialog::Instance()->Close();
    }
}


bool GUIRenderToy::renderColorPicker(const char* label, glm::vec3* color) {
    static std::unordered_map<std::string, bool> showColorPicker;
    bool changed = false;

    if (ImGui::ColorEdit3(label, &color->x)) {
        changed = true;  // Notifica un canvi de color
    }

    // Si es fa clic, alterna l'estat d'aquest color picker (obert/tancat)
    if (ImGui::IsItemClicked()) {
        showColorPicker[label] = !showColorPicker[label];
    }

    // Si el ColorPicker està obert, mostra'l
    if (showColorPicker[label]) {
        ImGui::SetNextWindowPos(ImGui::GetMousePos(), ImGuiCond_Appearing);
        ImGui::Begin(label, &showColorPicker[label], ImGuiWindowFlags_NoCollapse);
        if (ImGui::ColorPicker3("Escull un color", &color->x)) {
            changed = true; // Notifica el canvi de color
        }
        ImGui::End();
    }

    // Quan un color picker es tanca, força un refresc manualment
    if (!showColorPicker[label]) {
        glfwPostEmptyEvent();  // Notifica GLFW per evitar bloquejos
    }
    //ImGui::PopStyleColor(6);
    return changed;
}