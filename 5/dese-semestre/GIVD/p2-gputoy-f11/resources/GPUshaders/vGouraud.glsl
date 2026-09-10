#version 330 core
#define MAX_GPU_LIGHTS 8

/**
 * Gouraud Shading = Il·luminació per Vèrtex:
 * La característica principal del Gouraud Shading és que tota la lògica d'il·luminació
 * (aquí Blinn-Phong) s'executa una vegada per cada vèrtex de la geometria.
 * El color resultant es passa al fragment shader, que només l'interpola.
 */

layout (location = 0) in vec4 vPosition; // Posició en Model Space
layout (location = 1) in vec4 vColor; // No s'usa
layout (location = 2) in vec4 vNormal;   // Normal en Model Space
// layout (location = 3) in vec2 vTexCoords; // Descomentar si usem textures

// *** Uniforms ***
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

// Estructures per a la il·luminació i material
struct Lights
{
    vec3 Ia;
    vec3 Id;
    vec3 Is;
    bool enabled;
    vec3 pos; // Posició en World Space
    float a;  // Atenuació constant
    float b;  // Atenuació lineal
    float c;  // Atenuació quadràtica
};

struct Material {
    vec3 Ka;
    vec3 Kd;
    vec3 Ks;
    float shininess;
    float opacity;  // Opacitat (0.0 = transparent, 1.0 = opac)
};

uniform Lights lightComponents[MAX_GPU_LIGHTS];
uniform Material materialComponents;
uniform int numActiveLights;
uniform vec3 globalAmbientLight;
uniform vec3 viewPos; // Posició de la càmera en World Space, pel que sabem que treballem en coordenades món

// El color calculat per a aquest vèrtex
out vec4 color;
// out vec2 TexCoords; // Descomentar si passem coordenades de textura

void main()
{
    // 1. Posició del vèrtex en coordenades de món (World Space)
    vec3 FragPos = (modelMatrix * vPosition).xyz; // Posició del fragment en World Space

    // 2. Normal del vèrtex en coordenades de món (World Space)
    mat4 normalMatrix = transpose(inverse(modelMatrix));

    vec3 N = normalize(normalMatrix * vNormal).xyz;

    // 3. Vector del vèrtex a la càmera en World Space (fixem-nos que si no viewPos seria (0,0,0))
    vec3 V = normalize(viewPos - FragPos).xyz;

    // Inicialitzar color resultant amb la llum ambiental global
    vec3 result = globalAmbientLight * materialComponents.Ka;

    // Bucle per a cada llum
    for (int i = 0; i < MAX_GPU_LIGHTS; ++i)
    {
        if (lightComponents[i].enabled)
        {
            // Vector del vèrtex a la llum
            vec3 lightDir = lightComponents[i].pos - FragPos;
            float d = length(lightDir);
            vec3 L = normalize(lightDir); // Direcció normalitzada cap a la llum

            // Atenuació
            float denominator = lightComponents[i].a + lightComponents[i].b * d + lightComponents[i].c * d * d;
            float attenuation = (denominator > 0.0) ? (1.0 / denominator) : 1.0;

            // Components d'Il·luminació
            // Component ambiental
            vec3 ambient = lightComponents[i].Ia * materialComponents.Ka;

            // Component difusa
            float NL = max(dot(N, L), 0.0);
            vec3 diffuse = NL * lightComponents[i].Id * materialComponents.Kd;

            // Component especular (Blinn-Phong)
            vec3 H = normalize(L + V); // Vector Halfway
            float NH = max(dot(N, H), 0.0);
            float spec = pow(NH, materialComponents.shininess);
            vec3 specular = spec * lightComponents[i].Is * materialComponents.Ks;

            // Combinar components amb atenuació
            result += ambient + (diffuse + specular) * attenuation;
        }
    }

    // Passar el color calculat al fragment shader
    color = vec4(result, 1.0);

    // Passar les coordenades de textura si s'usen
    // TexCoords = vTexCoords;

    // Calcular la posició final en Clip Space per a la rasterització
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vPosition;
}