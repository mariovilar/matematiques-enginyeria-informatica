#pragma once

#include <GL/glew.h>
#include <string>
#include <fstream>
#include <sstream>
#include <iostream>
#include <vector>
#include <memory>
#include <filesystem>

class GPUShader {
    private:
        GLuint program;    // ID del programa de shader
        const char *name;

    public: 
        GPUShader(const char *name, const char *vertexPath, const char *fragmentPath);
        ~GPUShader();

        GLuint getId() { return program; }
        const char *getName() { return name; }
        void use() { glUseProgram(program); }

    // Funcions de suport a la càrrega dels shaders
        GLuint      initShader(const char* vname, const char* fname); // crea un programa amb dos shaders
        GLuint      compileShader(GLenum shaderType, const std::string& source, const std::string& typeName); 
        
        std::string readShaderSource(const std::string& filePath);
        void        checkCompileErrors(GLuint shader, std::string type);
        std::string getShaderPath(const std::string& filename);
};