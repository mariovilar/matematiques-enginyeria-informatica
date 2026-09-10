#version 330 core
#define MAX_GPU_LIGHTS 8

// Variables d'entrada (interpolades des del Vertex Shader)
in vec3 FragPos;  // Posició del fragment en World Space
in vec3 fNormal;   // Vector normal del fragment en World Space

// Sortida del shader
out vec4 FragColor;

// Estructura per a les llums
struct Lights
{
    vec3 Ia; // Ambient
    vec3 Id; // Diffuse
    vec3 Is; // Specular
    bool enabled;

    vec3 pos; // Posició en World Space
    float a;  // Atenuació constant
    float b;  // Atenuació lineal
    float c;  // Atenuació quadràtica
};

// Estructura per al material
struct Material {
    vec3 Ka; // Ambient
    vec3 Kd; // Diffuse
    vec3 Ks; // Specular
    float shininess; // Brillantor
    float opacity;  // Opacitat (0.0 = transparent, 1.0 = opac)
};

// Uniforms (valors constants per a aquest objecte/frame)
uniform Lights lightComponents[MAX_GPU_LIGHTS]; // Array de llums
uniform Material materialComponents;           // Material de l'objecte
uniform int numActiveLights;                   // Nombre de llums actives (opcional, pots iterar fins MAX_GPU_LIGHTS)
uniform vec3 globalAmbientLight;               // Llum ambiental global
uniform vec3 viewPos;                          // Posició de la càmera en World Space, pel que sabem que treballem en coordenades món
uniform float zThreshold;                       // Valor de llindar per a la Z

void main()
{
    // Normalitzar vectors importants (la interpolació pot afectar la longitud)
    vec3 N = normalize(fNormal);
    vec3 V = normalize(viewPos - FragPos); // Vector del fragment a la càmera

    // Inicialitzar color resultant amb la llum ambiental global
    vec3 result = globalAmbientLight * materialComponents.Ka;

    // Iterar per cada llum activa
    // Pots usar numActiveLights si el passes com a uniform, o iterar sempre fins MAX_GPU_LIGHTS
    for (int i = 0; i < MAX_GPU_LIGHTS; ++i)
    {
        if (lightComponents[i].enabled) // Processar només llums actives
        {
            // Calcular direcció de la llum i distància
            vec3 lightDir = lightComponents[i].pos - FragPos;
            float distance = length(lightDir);
            vec3 L = normalize(lightDir); // Normalitzar després de calcular la distància

            // Calcular atenuació
            float attenuation = 1.0;
            float denominator = lightComponents[i].a + lightComponents[i].b * distance + lightComponents[i].c * distance * distance;
            if (denominator > 0.0)
            {
                attenuation = 1.0 / denominator; // Atenuació inversa
            }
            // 1. Component Ambient (per llum)
            vec3 ambient = lightComponents[i].Ia * materialComponents.Ka;

            // 2. Component Difusa
            float NL = max(dot(N, L), 0.0);
            vec3 diffuse = NL * lightComponents[i].Id * materialComponents.Kd;

            // 3. Component Especular (Blinn-Phong)
            vec3 H = normalize(L + V); // Vector H (Halfway)
            float NH = max(dot(N, H), 0.0);
            float spec = pow(NH, materialComponents.shininess);
            vec3 specular = spec * lightComponents[i].Is * materialComponents.Ks;

            // Combinar components amb atenuació
            result += ambient + (diffuse + specular) * attenuation;
        }
    }
    if(FragPos.z < zThreshold)
    {
        float factor = result.x * result.x;
        factor += result.y * result.y;
        factor += result.z * result.z;
        factor = sqrt(factor);
        factor /= 3.0f;

        FragColor = vec4(vec3(factor), 1.0); // Alpha = 1.0 (opac)
    }
    else
    {
        FragColor = vec4(result, 1.0); // Alpha = 1.0 (opac)
    }
    
}