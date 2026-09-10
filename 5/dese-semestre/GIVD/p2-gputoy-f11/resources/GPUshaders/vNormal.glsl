#version 330 core
#define MAX_GPU_LIGHTS 8

layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 vColor; // Assuming color is at location 1
layout (location = 2) in vec4 vNormal; // Assuming normals are at location 2

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec3 fNormal; // Output normal to fragment shader

void main()
{
    // Transformem la normal a l'espai de món (per consistència amb els altres shaders)
    mat4 normalMatrix = transpose(inverse(modelMatrix));
    
    // Pass the light components to the fragment shader
    fNormal = (normalMatrix * vNormal).xyz;

    // Calculate final position
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition;
}