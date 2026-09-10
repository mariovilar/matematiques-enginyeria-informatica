#version 330 core

// Atributs d'entrada del vèrtex
layout (location = 0) in vec4 vPosition; 
layout (location = 1) in vec4 vColor;

// Aquí recollirem les varibles d'entrada del vertex shader
// i les passarem al fragment shader
// Propietats del material 
struct Material {
    vec3 Ka;
    vec3 Kd;
    vec3 Ks;
    float shininess;
    float opacity;
};

// Uniforms
uniform Material materialComponents;
uniform mat4 modelMatrix;      
uniform mat4 viewMatrix;       
uniform mat4 projectionMatrix;

out Material mat;

void main() {
    // Calculate world space position
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition;
    mat = materialComponents;
}