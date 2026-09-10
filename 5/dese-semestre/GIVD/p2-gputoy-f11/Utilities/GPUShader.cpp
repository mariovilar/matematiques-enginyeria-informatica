#include "GPUShader.hpp"

GPUShader::GPUShader(const char*name, const char *vertexPath, const char *fragmentPath)
{
    this->name = name;
    program = initShader(vertexPath, fragmentPath);
}

GPUShader::~GPUShader()
{
    glDeleteProgram(program);
}

GLuint GPUShader::initShader(const char* vname, const char* fname) {
    
        if (vname == NULL || fname == NULL) {
            return 0;
        }
    
        std::string vertexShaderSource = readShaderSource(vname);
        std::string fragmentShaderSource = readShaderSource(fname);
    
        if (vertexShaderSource.empty() || fragmentShaderSource.empty())
        {
            std::cerr << "No es poden llegir els fitxers de shaders \n";
            return -1;
        }
    
        // Compila vertex shader
        GLuint vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource, "VERTEX");
        if (vertexShader == 0) {
            return 0;
        }
        
        // Compila fragment shader
        GLuint fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource, "FRAGMENT");
        if (fragmentShader == 0) {
            glDeleteShader(vertexShader);
            return 0;
        }
    
        // Creació del programa de shaders
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
    
        // Link del programa
        glLinkProgram(program);
        checkCompileErrors(program, "PROGRAM");
    
        // Esborrar els shaders, ja no fan falta un cop linkats
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    
        return program;
    
}

std::string GPUShader::getShaderPath(const std::string& filename) {
    std::filesystem::path base_path = std::filesystem::current_path(); // Directori de treball
    std::filesystem::path shader_path = base_path / "resources" / "GPUshaders" / filename;

    if (!std::filesystem::exists(shader_path)) {
        std::cerr << "ERROR: No s'ha trobat el fitxer " << shader_path << std::endl;
        return "";
    }
    return shader_path.string();
}

std::string GPUShader::readShaderSource(const std::string& filePath)
{
    std::ifstream shaderFile(getShaderPath(filePath));
    if (!shaderFile.is_open())
    {
        std::cerr << "ERROR: No es pot obrir el fitxer glsl:  " << filePath << "\n";
        return "";
    }
    std::stringstream shaderStream;
    shaderStream << shaderFile.rdbuf();
    return shaderStream.str();
}

void GPUShader::checkCompileErrors(GLuint shader, std::string type)
{
    GLint success;
    GLchar infoLog[1024];
    if (type != "PROGRAM") {
        glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
        if (!success) {
            glGetShaderInfoLog(shader, 1024, NULL, infoLog);
            std::cerr << "ERROR::SHADER_COMPILATION_ERROR: " << type << "\n" << infoLog << "\n -- --------------------------------------------------- -- " << std::endl;
        }
    } else {
        glGetProgramiv(shader, GL_LINK_STATUS, &success);
        if (!success) {
            glGetProgramInfoLog(shader, 1024, NULL, infoLog);
            std::cerr << "ERROR::PROGRAM_LINKING_ERROR: " << type << "\n" << infoLog << "\n -- --------------------------------------------------- -- " << std::endl;
        }
    }
}


// Compile a shader of specified type
GLuint GPUShader::compileShader(GLenum shaderType, const std::string& source, const std::string& typeName) 
{
    GLuint shader = glCreateShader(shaderType);
    const char* sourceCStr = source.c_str();
    glShaderSource(shader, 1, &sourceCStr, NULL);
    glCompileShader(shader);
    
    // Check for errors
    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success) {
        checkCompileErrors(shader, typeName);
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}
