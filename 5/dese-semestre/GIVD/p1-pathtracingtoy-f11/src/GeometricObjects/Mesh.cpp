#include "GeometricObjects/Mesh.hpp"

Mesh::Mesh(const string &fileName): Object() {
    nom = fileName;
    load(fileName);
}

Mesh::~Mesh() {
    if (cares.size() > 0) cares.clear();
    if (vertexs.size() > 0) vertexs.clear();
}

void Mesh::makeTriangles() {
}

bool Mesh::hit (Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const {   
    return false;
}

bool Mesh::allHits(Ray& r, float tmin, float tmax, vector<ShadeInfo>& listShadeInfos) const {    
    return false;
}

void Mesh::aplicaTG(shared_ptr<TG> t) {    
}

void Mesh::update(int frame) {
}

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
        }

        // Si és una coordenada de textura (vt)
        else if (lineParts[0] == "vt") {
            // Placeholder per afegir textures si és necessari
        }

        // Si és una cara (f)
        else if (lineParts[0] == "f" && lineParts.size() >= 4) {
            Face face;
            for (size_t i = 1; i <= 3; ++i) {  // Assumeix triangles
                size_t pos = lineParts[i].find('/');
                int idx = (pos != std::string::npos) ? std::stoi(lineParts[i].substr(0, pos)) - 1 : std::stoi(lineParts[i]) - 1;
                face.idxVertices.push_back(idx);
            }
            cares.push_back(face);
        }
    }

    file.close();
}
