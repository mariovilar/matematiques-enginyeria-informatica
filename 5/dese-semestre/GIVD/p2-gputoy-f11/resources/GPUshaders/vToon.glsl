#version 330 core
layout (location = 0) in vec4 vPosition; // Posició del vèrtex (Model Space)
layout (location = 1) in vec4 vColor; // No s'usa
layout (location = 2) in vec4 vNormal;  // Normal del vèrtex (Model Space)
// layout (location = 3) in vec2 vTexCoords; // Si s'utilitzen textures

// Canviem els noms dels uniforms de matrius
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

// Variables per passar al Fragment Shader en World Space
out vec3 FragPos;   // Posició del fragment en coordenades de món
out vec3 fNormal;   // Normal en coordenades de món
// out vec2 TexCoords;

void main()
{
    // Calcula la posició en coordenades de món
    FragPos = (modelMatrix * vPosition).xyz; // Utilitzem modelMatrix, coordenades món
    // Transforma la normal a coordenades de món i la normalitza
    mat4 normalMatrix = transpose(inverse(modelMatrix)); // Utilitzem modelMatrix
    fNormal = (normalMatrix * vNormal).xyz; // Utilitzem vNormal i assignem a fNormal, coordenades món

    // Calcula la posició final del vèrtex per a la rasterització
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition; // Utilitzem les noves matrius

    // Passa les coordenades de textura (si s'utilitzen)
    // TexCoords = vTexCoords;
}
