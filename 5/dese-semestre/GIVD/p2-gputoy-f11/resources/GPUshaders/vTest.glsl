#version 330 core

layout (location = 0) in vec4 vPosition; // Posició del vèrtex (Model Space)
layout (location = 1) in vec4 vColor; // No s'usa
layout (location = 2) in vec4 vNormal;   // Normal del vèrtex (Model Space)

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
// No necessitem uniforms de llums, material o viewPos aquí, ja que ho farem des del fShader

// Variables de sortida cap al Fragment Shader
out vec3 FragPos;   // Posició del fragment en World Space
out vec3 fNormal;   // Normal del fragment en World Space

void main()
{
    // 1. Calcula la posició del fragment en World space
    FragPos = (modelMatrix * vPosition).xyz;

    // 2. Transform normal to World space (using the normal matrix: transpose(inverse(model)))
    mat4 normalMatrix = transpose(inverse(modelMatrix));
    
    // 3. Trobem el vector normal
    fNormal = (normalMatrix * vNormal).xyz;

    // 4. Calcula la posició final 
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition;
}

/**
 * Compte, perquè hem de saber si volem fer els càlculs a Espai de Món o Espai de Vista
 * Com que el nostre fragment shader (fPhong.glsl) espera rebre FragPos, Normal, viewPos 
 * i les posicions de les llums (lightComponents[i].pos) tot en World Space, el vertex shader (vPhong.glsl) 
 * ha de transformar la normal utilitzant només la modelMatrix (amb la seva inversa transposada) per passar-la en World Space.
 */