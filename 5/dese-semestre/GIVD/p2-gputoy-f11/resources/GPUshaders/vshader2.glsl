#version 330 core

layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 vColor;
layout (location = 2) in vec2 vCoordTexture;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec4 color;
out vec2 v_texcoord;

void main()
{
  // Calculate world space position
  gl_Position = projectionMatrix*viewMatrix*modelMatrix * vPosition;
  color = vColor;

  // Pas de les coordenades de textura al fragment shader
  v_texcoord = vCoordTexture;
  // El valor dels colors i les coordenades de textura s'interpolaran automaticament
  // en els pixels interiors a les cares dels polígons
   
}
