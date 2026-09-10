cmake_minimum_required(VERSION 3.10)

project(PathTracing VERSION 1.0 LANGUAGES CXX)

# Configuració de C++11
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
# Add a compiler flag
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -fPIC")

set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_INCLUDE_CURRENT_DIR ON)

cmake_policy(SET CMP0100 NEW)
# Make this a GUI application on Windows
if(WIN32)
    set(CMAKE_WIN32_EXECUTABLE ON)
endif()

# Afegir fitxers del projecte
# Afegir els fitxers font
file(GLOB SOURCES
    "src/*.cpp"
    "src/Utilities/*.cpp"
    "src/GeometricObjects/*.cpp"
    "src/Lights/*.cpp"
    "src/Materials/*.cpp"
    "src/Renders/*.cpp"
    "src/Shaders/*.cpp"
    "include/*.hpp"
    "include/Utilities/*.hpp"
    "include/GeometricObjects/*.hpp"
    "include/Lights/*.hpp"
    "include/Materials/*.hpp"
    "include/Renders/*.hpp"
    "include/Shaders/*.hpp"
    "external/imgui/*.cpp"            # Fitxers principals de Dear ImGui
    "external/imgui/backends/imgui_impl_glfw.cpp"    # Backend GLFW
    "external/imgui/backends/imgui_impl_opengl3.cpp" # Backend OpenGL
    "external/ImGuiFileDialog/ImGuiFileDialog.cpp" # File Dialog
    "external/ImGuiFileDialog/stb/*.cpp"            # File Dialog
    
)

# Find Packages
find_package(OpenGL REQUIRED)
find_package(GLEW REQUIRED)
find_package(glfw3 REQUIRED)

find_package(PkgConfig REQUIRED)

# find the GLFW package
pkg_search_module(GLFW REQUIRED glfw3)

# Paths per a Homebrew (macOS)
if(APPLE)
    execute_process(COMMAND brew --prefix glew OUTPUT_VARIABLE GLEW_PREFIX OUTPUT_STRIP_TRAILING_WHITESPACE)

    #set(GLEW_INCLUDE_DIR /opt/homebrew/include)
    #set(GLEW_LIBRARY /opt/homebrew/lib/libGLEW.a)
    set(GLEW_INCLUDE_DIR ${GLEW_PREFIX}/include)
    set(GLEW_LIBRARY ${GLEW_PREFIX}/lib/libGLEW.a)
endif()

# Llibreries externes
include_directories(
    include 
    include/Utilities 
    include/GeometricObjects 
    include/Lights 
    include/Materials 
    include/Renders 
    include/Shaders 
    external 
    external/imgui 
    external/ImGuiFileDialog 
    external/glm 
    external/ImGuifileDialog/stb
    /usr/local/include/
    )


# Crear executable
add_executable(PathTracing ${SOURCES})

target_link_libraries(PathTracing
${GLEW_LIBRARY}
${GLFW_LIBRARIES}
${OPENGL_LIBRARIES}
)
