#include "Mesh.hpp"

Mesh::Mesh(const string &fileName): Object() {
    nom = fileName;
    load(fileName);
}

/**
 * @brief Mesh::~Mesh()
 */
Mesh::~Mesh() {
    if (cares.size() > 0) cares.clear();
    if (vertexs.size() > 0) vertexs.clear();
}

void Mesh::make() {

    // Netejar vectors abans de tornar a omplir (per si make es crida més d'una vegada)
    // Neteja els vectors que s'utilitzaran per enviar les dades a la GPU (els buffers finals)
    objectVertices.clear();
    objectNormals.clear();  
    objectTextures.clear(); 
    objectColors.clear();

    // Comprovar si tenim les dades originals carregades
    if (cares.empty() || vertexs.empty()) {
        std::cerr << "Error a Mesh::make(): No hi ha cares o vèrtexs carregats." << std::endl;
        return;
    }

    // Cal passar els vertexs, les cares i les normals a les
    // estructures per ser llegides a la GPU en els buffers corresponents
    for (int i = 0; i < cares.size(); i++) {

        // Per cada cara, agafem els vertexs i les normals
        Face f = cares[i];
        
        for (int j = 0; j < f.idxVertices.size(); j++) {

            // Cal posar els vertexs als vectors de dades per poder passar-los a la GPU
            int idxV = f.idxVertices[j];
            objectVertices.push_back(vertexs[idxV]);

            // Cal posar les normals per poder passar-les a la GPU
            int idxN = f.idxNormals[j];
            objectNormals.push_back(normalsVertexs[idxN]);

            // Assignem un color aleatori a cada vertex (no ho borrem però podríem)
            vec4 color = vec4((rand() % 100) / 100.0, (rand() % 100) / 100.0, (rand() % 100) / 100.0, 1.0);
            objectColors.push_back(color);

             // I cal posar les coordenades de textura als vectors de dades 
            // per poder passar-les a la GPU
            if (f.idxTextures.size() > 0) {
                // Si no hi ha textures, no cal fer res
                // Si hi ha textures, agafem les coordenades de textura
                // i les afegim al vector de textures
                int idxT = f.idxTextures[j];
                objectTextures.push_back(textVertexs[idxT]);
            }
        }
    }

    vec3 Ka = vec3(0.2f, 0.1f, 0.1f);
    vec3 Kd = vec3(0.2f, 0.8f, 0.4f);
    vec3 Ks = vec3(0.5f, 0.5f, 0.5f);
    float shininess = 300.0f;
    setMaterial(make_shared<GPUMaterial>(Ka, Kd, Ks, shininess));
}

// TODO: Funcions per carrega de la mesh des d'un .obj

// Funció per eliminar espais al principi i final d'una cadena
void trim(std::string &str) {
    size_t start = str.find_first_not_of(" \t\r\n");
    size_t end = str.find_last_not_of(" \t\r\n");
    str = (start == std::string::npos || end == std::string::npos) ? "" : str.substr(start, end - start + 1);
}

// Funció per dividir una cadena en parts utilitzant espais com a separador
std::vector<std::string> split(const std::string &str) {
    std::vector<std::string> tokens;
    std::istringstream stream(str);
    std::string token;
    while (stream >> token) {
        tokens.push_back(token);
    }
    return tokens;
}

// Funció per llegir el fitxer i processar el contingut
void Mesh::load(const std::string &fileName) {
    std::ifstream file(fileName);
    if (!file) {
        std::cerr << "Boundary object file not found.\n";
        return;
    }

    // Neteja els vectors que emmagatzemen les dades llegides directament del fitxer
    vertexs.clear();
    cares.clear();
    normalsVertexs.clear();
    textVertexs.clear();

    std::string line;
    while (std::getline(file, line)) {
        trim(line);
        if (line.empty()) continue;

        std::vector<std::string> lineParts = split(line);
        if (lineParts.empty()) continue;

        // Si és un comentari
        if (lineParts[0] == "#") {
            // std::cout << "Comment: " << line.substr(1) << std::endl;
        }

        // Si és un vèrtex (v)
        else if (lineParts[0] == "v" && lineParts.size() >= 4) {
            vertexs.push_back(glm::vec4(
                std::stof(lineParts[1]), 
                std::stof(lineParts[2]), 
                std::stof(lineParts[3]), 
                1.0f
            ));
        }

        // Si és una normal (vn)
        else if (lineParts[0] == "vn") {
            // Placeholder per afegir normals si és necessari
            normalsVertexs.push_back(glm::vec4(
                std::stof(lineParts[1]), 
                std::stof(lineParts[2]), 
                std::stof(lineParts[3]), 
                1.0f
            ));
        }

        // Si és una coordenada de textura (vt)
        else if (lineParts[0] == "vt") {
            // Placeholder per afegir textures si és necessari
            textVertexs.push_back(glm::vec2(
                std::stof(lineParts[1]), 
                std::stof(lineParts[2])
            ));
        }

        // Si és una cara (f)
        else if (lineParts[0] == "f" && lineParts.size() >= 4) {
            Face face;
            for (size_t i = 1; i <= 3; ++i) {  // Assumeix triangles
                std::string token = lineParts[i];  // Cada token és, per exemple, "1/2/3" o "1//3"
                
                int idxVertex = -1;
                int idxTexture = -1;
                int idxNormal  = -1;
                
                // Troba la primera barra
                size_t pos1 = token.find('/');
                if (pos1 != std::string::npos) {
                    // Extreiem l'índex del vèrtex (recorda: .obj utilitza índexs basats en 1)
                    idxVertex = std::stoi(token.substr(0, pos1)) - 1;
                    
                    // Troba la segona barra
                    size_t pos2 = token.find('/', pos1 + 1);
                    if (pos2 != std::string::npos) {
                        // Si hi ha contingut entre la primera i la segona barra, és la textura
                        if (pos2 > pos1 + 1)
                            idxTexture = std::stoi(token.substr(pos1 + 1, pos2 - pos1 - 1)) - 1;
                        // Si no hi ha contingut, idxTexture quedarà en -1
                        
                        // La resta de la cadena (després de la segona barra) és la normal
                        if (token.length() > pos2 + 1)
                            idxNormal = std::stoi(token.substr(pos2 + 1)) - 1;
                    } else {
                        // Si només hi ha una barra, assumim que és vertex/texture (sense normal)
                        idxTexture = std::stoi(token.substr(pos1 + 1)) - 1;
                    }
                } else {
                    // No hi ha barres: només tenim l'índex del vèrtex
                    idxVertex = std::stoi(token) - 1;
                }
                
                face.idxVertices.push_back(idxVertex);
                if (idxTexture!=-1) face.idxTextures.push_back(idxTexture);
                if (idxNormal!=-1) face.idxNormals.push_back(idxNormal);
            }
            cares.push_back(face);            
        }
    }
    file.close();
}


