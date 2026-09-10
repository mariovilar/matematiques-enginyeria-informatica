#include <iostream>
#include <vector>

#define GL_SILENCE_DEPRECATION

#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "imgui.h"
#include "backends/imgui_impl_glfw.h"
#include "backends/imgui_impl_opengl3.h"

#include "GUITracerToy.hpp"
#include "Controller.hpp"


using namespace glm;
using namespace std;

// Configuració inicial
const int WINDOW_WIDTH = 800;
const int WINDOW_HEIGHT = 600;


int main() {
    // 1. Inicialització de GLFW
    if (!glfwInit()) {
        cerr << "Failed to initialize GLFW\n";
        return -1;
    }
    cout << "Path Tracing inicialitzat" << endl;

    // 2. Configuració d'OpenGL per Legacy (GLFW 2.1)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
    
    // 3. Creació de la finestra de Rendering
    GLFWwindow* window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Amazing Rendering", nullptr, nullptr);
    if (!window) {
        std::cerr << "Error al crear la finestra de GLFW\n";
        glfwTerminate();
        return -1;
    }
    glfwSetWindowAttrib(window, GLFW_RESIZABLE, GLFW_FALSE);
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);  // Sincronització vertical per evitar tearing
    int fbWidth, fbHeight;
    glfwGetFramebufferSize(window, &fbWidth, &fbHeight);
    glViewport(0, 0, fbWidth, fbHeight);
    glfwSetCursorPosCallback(window, nullptr);  // Això fa que GLFW ignori els moviments del ratolí

    // 4. Creació de la finestra de la GUI
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
    GLFWwindow* guiWindow = glfwCreateWindow(700, 700, "Configuració - Path Tracing", nullptr, window);
    if (!guiWindow) {
        std::cerr << "Error al crear la finestra de la GUI\n";
        glfwTerminate();
        return -1;
    }
    glfwShowWindow(guiWindow);

    // 5. Inicialització de Dear ImGui per a la finestra de la GUI
    glfwMakeContextCurrent(guiWindow);
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGui_ImplGlfw_InitForOpenGL(guiWindow, true);
    ImGui_ImplOpenGL3_Init("#version 120"); // Compatible amb OpenGL 2.1

    // 6. Creació de la GUI i inicialització de la configuració
    GUITracerToy *gui = new GUITracerToy(fbWidth, fbHeight);

    // 7. Inicialitzacio del món virtual per defecte dins del contructor del controller
    if (Controller::getInstance()) {
        // Posem la configuració de la GUI al controlador
        Controller::getInstance()->setSetup(gui->getSetup());
    } else {
        cerr << "Error al crear el controlador" << endl;
        return -1;
    }

    // 8. Bucle principal d'events d'ambdues finestres
    while (!glfwWindowShouldClose(window) && !glfwWindowShouldClose(guiWindow)) {
        // Processament d'events
        glfwPollEvents();

        // Renderitza el món (OpenGL) a la finestra de rendering
        glfwMakeContextCurrent(window); 
        
        if ( Controller::getInstance()->needsToRender) {
            Controller::getInstance()->renderWorld();
            glfwSwapBuffers(window);
        }

        // Renderitza la GUI a la segona finestra
        glfwMakeContextCurrent(guiWindow);
        ImGui_ImplOpenGL3_NewFrame();
        ImGui_ImplGlfw_NewFrame();
        ImGui::NewFrame();

        gui->renderConfigGUI();
        
        ImGui::Render();
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
        glfwSwapBuffers(guiWindow);
    }

    // Alliberar recursos
    ImGui_ImplOpenGL3_Shutdown();
    ImGui_ImplGlfw_Shutdown();
    ImGui::DestroyContext();

    glfwDestroyWindow(window);
    glfwDestroyWindow(guiWindow);
    glfwTerminate();

    return 0;
}
