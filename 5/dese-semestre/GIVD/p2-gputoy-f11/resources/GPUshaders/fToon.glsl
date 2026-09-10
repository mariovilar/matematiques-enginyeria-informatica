#version 330 core
#define MAX_GPU_LIGHTS 8 // Nombre màxim de llums GPU
out vec4 FragColor;

// Variables rebudes del Vertex Shader (en World Space)
in vec3 FragPos;  // Posició del fragment en coordenades de món
in vec3 fNormal;   // Normal en coordenades de món

// Paràmetres per a Toon Shading
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
uniform int numActiveLights;                   // Nombre de llums actives
uniform vec3 globalAmbientLight;               // Llum ambiental global
uniform vec3 viewPos;                          // Posició de la càmera en World Space, pel que sabem que treballem en coordenades món

void main()
{
    vec3 N = normalize(fNormal);
    vec3 V = normalize(viewPos - FragPos); // Vector cap a la càmera

    // Component ambient global
    vec3 result = globalAmbientLight * materialComponents.Ka;

    for (int i = 0; i < MAX_GPU_LIGHTS; i++)
    {
        if (lightComponents[i].enabled) {
        
            // Component ambient de la llum
            vec3 ambient = lightComponents[i].Ia * materialComponents.Ka;

            vec3 L = normalize(lightComponents[i].pos - FragPos);
            float diff = max(dot(N, L), 0.0);
            float toonDiffuse;
            
            // Alguns casos per al toon diffuse
            if (diff > 0.95)
                toonDiffuse = 1.0;
            if (diff > 0.75)
                toonDiffuse = 0.9;
            else if (diff > 0.6)
                toonDiffuse = 0.8;
            else if (diff > 0.5)
                toonDiffuse = 0.7;
            else if (diff > 0.4)
                toonDiffuse = 0.6;
            else if (diff > 0.35)
                toonDiffuse = 0.5;
            else if (diff > 0.28)
                toonDiffuse = 0.4;
            else if (diff > 0.15)
                toonDiffuse = 0.25;
            else
                toonDiffuse = 0.2;

            // Color difús
            vec3 diffuse = toonDiffuse * lightComponents[i].Id * materialComponents.Kd;

            // Descartem la component especular per donar-li un aspecte més "cartoon"
            result += ambient + diffuse;
        }
    }

    FragColor = vec4(result, 1.0);
}

// Té una estructura molt similar a la de Phong, però amb un shader de fragment diferent, ja que
// el Toon Shading en lloc d'utilitzar directament els resultats (diff, spec) són discretitzats en franges