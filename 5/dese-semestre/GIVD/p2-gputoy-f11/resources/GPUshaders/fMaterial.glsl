#version 330

struct Material {
  vec3 Ka;
  vec3 Kd;
  vec3 Ks;
  float shininess;
  float opacity;
};

in Material mat;
out vec4 colorOut;

void main()
{
  colorOut = vec4(mat.Kd, 1.0);
}