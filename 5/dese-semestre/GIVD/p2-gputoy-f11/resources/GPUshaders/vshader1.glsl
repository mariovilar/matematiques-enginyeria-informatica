#version 330 core

layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 vColor;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

#define MAX_GPU_LIGHTS 8

out vec4 color; // Output color to fragment shader

void main() {
    // Calculate world space position
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition;

    // Set the color to the vertex color
    color = vColor;
    
}