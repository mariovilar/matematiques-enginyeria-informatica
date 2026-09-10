#include "Object.hpp"

Object::Object()
{
    material = nullptr;
    textureID = 0;
}

void Object::setMaterial(shared_ptr<GPUMaterial> m) {
    material = m;
    if(!this->isTextured()) {
        textureID = 0;   // Si el material té textura, descartar-la aquí no té sentit
    }
}

shared_ptr<GPUMaterial> Object::getMaterial() {
    return material;
}

void Object::materiaToGPU() {
    material->toGPU(program);
}

void Object::generateTextureCoordinates() {
    objectTextures.clear();

    if (objectVertices.empty()) return;

    // Calculem la bounding box
    vec3 minPt(FLT_MAX), maxPt(-FLT_MAX);
    for (const auto& v : objectVertices) {
        vec3 pos(v.x, v.y, v.z);
        minPt = glm::min(minPt, pos);
        maxPt = glm::max(maxPt, pos);
    }

    vec3 center = (minPt + maxPt) * 0.5f;

    // Afegim les coordenades de textura
    for (const auto& vertex : objectVertices) {
        vec3 p = glm::normalize(vec3(vertex.x, vertex.y, vertex.z) - center);

        float u = 0.5f - atan2(p.z, p.x) / (2.0f * glm::pi<float>());
        float v = 0.5f - asin(p.y) / glm::pi<float>();

        objectTextures.push_back(glm::vec2(u, v));
    }
}


void Object::toGPU(GLuint p)
{
    this->program = p;
    
    glGenVertexArrays(1, &VAO);
	glGenBuffers(1, &vertex_buffer);
    glGenBuffers(1, &color_buffer);
    glGenBuffers(1, &normal_buffer);

    glBindVertexArray(VAO);
	// Bind vertices to layout location 0
	glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer );
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectVertices.size(), &objectVertices[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(0); // This allows usage of layout location 0 in the vertex shader
	glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

	// Bind colors to layout location 1
	glBindBuffer(GL_ARRAY_BUFFER, color_buffer );
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectColors.size(), &objectColors[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(1); // This allows usage of layout location 1 in the vertex shader
	glVertexAttribPointer(1, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

    // Bind normals to layout location 2
    glBindBuffer(GL_ARRAY_BUFFER, normal_buffer);
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectNormals.size(), &objectNormals[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(2); // This allows usage of layout location 2 in the vertex shader
	glVertexAttribPointer(2, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);

}

void Object::draw()
{
    // Si hi ha material, l'hem d'enviar a la GPU
    // Això assegura que les propietats del material (uniforms) 
    // s'estableixin al shader correcte cada vegada que l'objecte es dibuixa.
    if (material != nullptr) {
        material->toGPU(program);
    }

    // Si hi ha textura, l'hem d'enviar a la GPU
    if (textureID != 0) {
        rebindTexture();
    }
    
    glBindVertexArray(VAO);
    glDrawArrays(GL_TRIANGLES, 0, objectVertices.size());
    glBindVertexArray(0);
}

// Mètodes per tractar les textures
// Metode per inicialitzar la textura un cop llegida de disc
void Object::initTextureGL(const char *filename)
{
    if (textureID != 0) {
        glDeleteTextures(1, &textureID);
    }

    glGenTextures(1, &textureID);
    glBindTexture(GL_TEXTURE_2D, textureID);
    
    // Set texture parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
    // Load image, create texture and generate mipmaps
    int width, height, nrChannels;
    stbi_set_flip_vertically_on_load(true); // flip y-axis during image loading
    unsigned char *data = stbi_load(filename, &width, &height, &nrChannels, 0);
    
    if (data) {
        if (nrChannels == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        } else if (nrChannels == 4) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        }
        glGenerateMipmap(GL_TEXTURE_2D);
    } else {
        std::cerr << "Failed to load texture: " << filename << std::endl;
    }
    
    stbi_image_free(data);

    // Optionally store the texture ID in your cub object for later use
}


// Reactivar la textura en cas de canvi de shader al shader de textura
void Object::rebindTexture() 
{
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textureID);
    
    // Set texture sampler uniform
    GLuint textureLoc = glGetUniformLocation(program, "textureMap");
    if (textureLoc != -1) {
        glUniform1i(textureLoc, 0);
    }
}

// Mètode per passar els vertexs de textura a la GPU
void Object::toGPUTexture(GLuint p)
{
    this->program = p;
    
    glGenVertexArrays(1, &VAO);
	glGenBuffers(1, &vertex_buffer);
	glGenBuffers(1, &color_buffer);
    glGenBuffers(1, &normal_buffer);
    glGenBuffers(1, &texture_buffer);

    glBindVertexArray(VAO);
	// Bind vertices to layout location 0
	glBindBuffer(GL_ARRAY_BUFFER, vertex_buffer );
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectVertices.size(), &objectVertices[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(0); // This allows usage of layout location 0 in the vertex shader
	glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

	// Bind colors to layout location 1
	glBindBuffer(GL_ARRAY_BUFFER, color_buffer );
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectColors.size(), &objectColors[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(1); // This allows usage of layout location 1 in the vertex shader
	glVertexAttribPointer(1, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

    // Bind texture coordinates to layout location 2
    glBindBuffer(GL_ARRAY_BUFFER, normal_buffer);
	glBufferData(GL_ARRAY_BUFFER, sizeof(vec4) * objectNormals.size(), &objectNormals[0], GL_STATIC_DRAW);
	glEnableVertexAttribArray(2); // This allows usage of layout location 3 in the vertex shader
	glVertexAttribPointer(2, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);

    // Bind texture coordinates to layout location 3
    glBindBuffer(GL_ARRAY_BUFFER, texture_buffer );
    glBufferData(GL_ARRAY_BUFFER, sizeof(vec2) * objectTextures.size(), &objectTextures[0], GL_STATIC_DRAW);
    glEnableVertexAttribArray(3); // This allows usage of layout location 2 in the vertex shader
    glVertexAttribPointer(3, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), 0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindVertexArray(0);
}