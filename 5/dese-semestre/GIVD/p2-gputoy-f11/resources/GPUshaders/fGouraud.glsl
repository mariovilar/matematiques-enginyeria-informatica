/**
 * Fragment Shader (en Gouraud): El fragment shader en un esquema Gouraud és molt simple.
 * Només rep el color interpolat (in) des del vertex shader i l'assigna directament a la sortida (FragColor). 
 * No fa cap càlcul d'il·luminació.
 */
#version 330

in vec4 color; // Color interpolat del vertex shader
out vec4 colorOut;

void main()
{
  colorOut = color;
}
