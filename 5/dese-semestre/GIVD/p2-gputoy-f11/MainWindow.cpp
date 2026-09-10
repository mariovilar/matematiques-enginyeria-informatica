
#include <iostream>
#include "GUIRenderToy.hpp"
#include "GLWidget.hpp"

#define GL_SILENCE_DEPRECATION

// Inicialitzacions de GL
bool initializeGLFW();
bool initializeGLEW();
GLFWwindow* createWindow(int width, int height, const char* title);

// Funcions de renderització
void startImGuiFrame();
void refreshGUI();

// Funcions de neteja
void cleanup(GLFWwindow* window, GUIRenderToy& gui);

// Callbacks d'events
void setupCallbacks(GLFWwindow* window);
void glfw_error_callback(int error, const char* description);
void mouse_button_callback(GLFWwindow* window, int button, int action, int mods);
void cursor_position_callback(GLFWwindow* window, double xpos, double ypos);

GLWidget *glWidgetPtr = nullptr;


int main(int argc, char *argv[])
{
    // Initialize GLFW and create window
    if (!initializeGLFW())
        return -1;
    
    // Create window with graphics context
    GLFWwindow* window = createWindow(1600, 1200, "GPU-ZBuffer GPU - 2024-25");
    if (window == NULL)
        return -1;
    
    // Initialize GLEW
    if (!initializeGLEW())
        return -1;

    int display_w, display_h; 
    glfwGetFramebufferSize(window, &display_w, &display_h);
    // Setup GUI system
    GUIRenderToy gui;
    GPUConfig config(display_w, display_h);
    gui.setConfig(config);
    gui.setup_imgui(window);

    // Inicialització del widget OpenGL segons les dimensions de la finestra

    GLWidget glWidget(display_w, display_h);
    glWidgetPtr = &glWidget;
    glWidget.initializeGL();

    // Set up callbacks
    setupCallbacks(window);
    
    // Main rendering loop
    while (!glfwWindowShouldClose(window)) {
        // Poll for events
        glfwPollEvents();
        
        // Begin new ImGui frame
        startImGuiFrame();
        
        // Calculate viewport dimensions
        int display_w, display_h;
        glfwGetFramebufferSize(window, &display_w, &display_h);
        float current_vp_width = display_w/4;
        float current_vp_height = display_h/2;
        
        // Position the ImGui window
        ImGui::SetNextWindowPos({0, 10});
        ImGui::SetNextWindowSize({current_vp_width, current_vp_height}); 

        // Render ImGui interface
        gui.render_imgui(glWidget);
        
        // Clear buffers and render OpenGL content
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glWidget.paintGL();
        
        ImGui::End();
        
        // Refresh ImGui
        refreshGUI();
       
        // Swap buffers
        glfwSwapBuffers(window);
    }

    // Clean up
    cleanup(window, gui);

    return 0;
}

// Initialize GLFW with error callback
bool initializeGLFW() {
    glfwSetErrorCallback(glfw_error_callback);
    if (!glfwInit()) {
        std::cerr << "Failed to initialize GLFW\n";
        return false;
    }
    return true;
}

// Create and configure window
GLFWwindow* createWindow(int width, int height, const char* title) {
    // Configure OpenGL 3.3 Core Profile
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
#ifdef __APPLE__
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif

    // Create window
    GLFWwindow* window = glfwCreateWindow(width, height, title, NULL, NULL);
    if (window == NULL) {
        std::cerr << "Failed to create GLFW window\n";
        glfwTerminate();
        return NULL;
    }
    
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // Enable vsync
    
    return window;
}

// Initialize GLEW
bool initializeGLEW() {
    if (glewInit() != GLEW_OK) {
        std::cerr << "Failed to initialize GLEW\n";
        return false;
    }
    return true;
}

// Set up necessary callbacks
void setupCallbacks(GLFWwindow* window) {
    glEnable(GL_DEPTH_TEST);
    glfwSetMouseButtonCallback(window, mouse_button_callback);
    glfwSetCursorPosCallback(window, cursor_position_callback);
}

// Start a new ImGui frame
void startImGuiFrame() {
    ImGui_ImplOpenGL3_NewFrame();
    ImGui_ImplGlfw_NewFrame();
    ImGui::NewFrame();
}

// Render ImGui
void refreshGUI() {
    ImGui::Render();
    ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
}

// Clean up resources
void cleanup(GLFWwindow* window, GUIRenderToy& gui) {
    gui.cleanup_imgui();
    glfwDestroyWindow(window);
    glfwTerminate();
}



// Callbacks

void glfw_error_callback(int error, const char* description)
{
    std::cerr << "Glfw Error " << error << ": " << description << std::endl;
}

void mouse_button_callback(GLFWwindow* window, int button, int action, int mods)
{
    // Passa l'esdeveniment a ImGui
    ImGui_ImplGlfw_MouseButtonCallback(window, button, action, mods);

    // Comprova si algun widget d'ImGui necessita capturar l'entrada del ratolí
    if (ImGui::GetIO().WantCaptureMouse)
        return;  // No processar l'esdeveniment per a la finestra OpenGL

    if (glWidgetPtr) {
        glWidgetPtr->mousePressEvent(window, button, action, mods);
    }
}

void cursor_position_callback(GLFWwindow* window, double xpos, double ypos)
{
    // Forward de l'event a ImGui
    ImGui_ImplGlfw_CursorPosCallback(window, xpos, ypos);

    // Si ImGui necessita capturar el ratolí, no processem més
    if (ImGui::GetIO().WantCaptureMouse)
        return;

    if (glWidgetPtr) {
        glWidgetPtr->mouseMoveEvent(window, xpos, ypos);
    }
}


    