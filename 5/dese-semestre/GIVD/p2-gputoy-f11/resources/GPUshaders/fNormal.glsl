#version 330
#define MAX_GPU_LIGHTS 8

in vec3 fNormal;
out vec4 colorOut;

void main()
{
  // Normalitzem la normal rebuda
  vec3 normalColor = normalize(fNormal) * 0.5 + 0.5;

  // Assigna el color calculat a la sortida del fragment shader.
  colorOut = vec4(normalColor, 1.0);
}