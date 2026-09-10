#version 330

in vec4 color;
in vec2 v_texcoord;
out vec4 colorOut;

uniform sampler2D textureMap;

void main()
{
  colorOut = vec4(texture(textureMap, v_texcoord).rgb, 1.0f);
}

