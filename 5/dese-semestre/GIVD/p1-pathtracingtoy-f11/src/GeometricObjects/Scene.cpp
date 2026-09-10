#include "GeometricObjects/Scene.hpp"
// No sabem per què però si no l'importem no funciona
#include "../../include/Materials/Transparent.hpp"
#include "../../include/Materials/MaterialTextura.hpp"

// Gestió textura
#include <filesystem>
#include <iostream>

Scene::Scene()
{
    init();
}

bool Scene::hit(Ray &r, float tmin, float tmax, ShadeInfo &shadeInfo) const
{
    // Si no hi ha cap objecte retornem false
    if (objects.empty())
        return false;

    bool hit = false;

    // Iterem per a cada objecte en la escena
    for (size_t i = 0; i < objects.size(); ++i)
    {
        if (objects[i]->hit(r, tmin, tmax, shadeInfo))
        {
            hit = true;
            tmax = shadeInfo.t;
        }
    }
    return hit;
}

bool Scene::allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo> &listShadeInfos) const
{
    // Heu de codificar la vostra solucio per aquest metode substituint el 'return false'
    // Una possible solucio es cridar el mètode "hit" per a tots els objectes i quedar-se amb totes
    // les interseccions
    // Si un objecte es intersecat pel raig entre tmin i tmax, el parametre  de tipus ShadeInfo conte
    // la informació sobre la interseccio.
    // Cada vegada que s'intersecta un objecte s'ha d'afegir un nou ShadeInfo a la llista de ShadeInfos
    if (objects.empty())
        return false;

    bool hit = false;
    ShadeInfo shadeInfo;

    for (size_t i = 0; i < objects.size(); ++i)
    {
        if (objects[i]->hit(r, tmin, tmax, shadeInfo))
        {
            hit = true;
            listShadeInfos.push_back(shadeInfo);
        }
    }
    return hit;
}

void Scene::update(int nframe)
{
    for (unsigned int i = 0; i < objects.size(); i++)
    {
        objects[i]->update(nframe);
    }
}

void Scene::aplicaTG(shared_ptr<TG> tg)
{
    // TODO
}

void Scene::test1()
{

    shared_ptr<Metal> metal = make_shared<Metal>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.0f), glm::vec3(1.0f, 1.0f, 1.0f), 100.0f);

    objects.clear();
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.0f, 0.0f, -5.0f), 1.0f, glm::vec3(1.0f, 0.0f, 0.0f)));  // Esfera vermella
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.0f, -100.5f, -5.0f), 100.0f, metal));                   // Esfera gran groguenca
    objects.push_back(std::make_shared<Sphere>(glm::vec3(2.0f, 1.0f, -6.0f), 1.5f, glm::vec3(0.0f, 1.0f, 0.0f)));  // Esfera verda
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-2.0f, 1.0f, -4.0f), 1.0f, glm::vec3(0.0f, 0.0f, 1.0f))); // Esfera blava
}

void Scene::test2()
{
    shared_ptr<Lambertian> lam1 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(1.0f, 1.0f, 1.0f), 1.f, 1.f);
    shared_ptr<Lambertian> lam2 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.90f, 0.76f, 0.46f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.0f);
    shared_ptr<Lambertian> lam3 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.65f, 0.77f, 0.97f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.0f);
    shared_ptr<Lambertian> lam4 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.9f, 0.9f, 0.9f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.0f);
    shared_ptr<Transparent> trans = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.32f, 0.36f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.5f);

    objects.clear();
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, -10004.0f, -20.0f), 10000.0f, lam1));
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, 0.0f, -20.0f), 4.0f, trans));
    objects.push_back(std::make_shared<Sphere>(vec3(5.0f, -1.0f, -15.0f), 2.0f, lam2));
    objects.push_back(std::make_shared<Sphere>(vec3(5.0f, 0.0f, -25.0f), 3.0f, lam3));
    objects.push_back(std::make_shared<Sphere>(vec3(-5.5f, 0.0f, -15.0f), 3.0f, lam4));
}

void Scene::test3()
{
    shared_ptr<Lambertian> lam1 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(1.0f, 1.0f, 1.0f), 1.f, 1.f);
    shared_ptr<Metal> met1 = make_shared<Metal>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.90f, 0.76f, 0.46f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.0f);
    shared_ptr<Lambertian> lam3 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.65f, 0.77f, 0.97f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.0f);
    shared_ptr<Metal> met2 = make_shared<Metal>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.9f, 0.9f, 0.9f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.0f);
    shared_ptr<Transparent> trans = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.32f, 0.36f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.25f);

    objects.clear();
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, -10004.0f, -20.0f), 10000.0f, lam1));
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, 0.0f, -20.0f), 4.0f, trans));
    objects.push_back(std::make_shared<Sphere>(vec3(5.0f, -1.0f, -15.0f), 2.0f, met1));
    objects.push_back(std::make_shared<Sphere>(vec3(5.0f, 0.0f, -25.0f), 3.0f, lam3));
    objects.push_back(std::make_shared<Sphere>(vec3(-5.5f, 0.0f, -15.0f), 3.0f, met2));
}

void Scene::test4()
{
    shared_ptr<Lambertian> lam1 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.8f), glm::vec3(1.0f, 1.0f, 1.0f), 10.f, 1.f);
    shared_ptr<Transparent> trans1 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.1f, 0.2f, 0.5f), glm::vec3(1.0f, 1.0f, 1.0f), 10.f, 0.3f);
    trans1->setNu(1.0f);
    trans1->setDmax(8.0f);
    shared_ptr<Transparent> trans2 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.8f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.3f);
    trans2->setNu(1.0f);
    trans2->setDmax(8.0f);
    shared_ptr<Transparent> trans3 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.2f, 0.2f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.2f);
    trans3->setNu(1.0f);
    trans3->setDmax(8.0f);

    objects.clear();
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, -100.5f, -1.0f), 100.0f, lam1));
    objects.push_back(std::make_shared<Sphere>(vec3(0.0f, 0.0f, -1.2f), 0.5f, trans1));
    objects.push_back(std::make_shared<Sphere>(vec3(-1.0f, 0.0f, -1.0f), 0.5f, trans2));
    objects.push_back(std::make_shared<Sphere>(vec3(1.0f, 0.0f, -1.0f), 0.5f, trans3));
}

void Scene::test5()
{
    std::filesystem::path base_path = std::filesystem::current_path().parent_path();
    std::filesystem::path texture_path = base_path / "resources" /  "pintura.jpg";
    shared_ptr<MaterialTextura> matText = make_shared<MaterialTextura>(texture_path.c_str());
    objects.clear();
    objects.push_back(std::make_shared<Box>(vector<vec3>({vec3(-1.0, -1.0, -1.0), vec3(1.0, 1.0, 1.0)}), matText));
}

void Scene::finalImage()
{
    // shared_ptr<Lambertian> lam1 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.8f), glm::vec3(1.0f, 1.0f, 1.0f), 10.f, 1.f);
    shared_ptr<Transparent> trans1 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.1f, 0.2f, 0.5f), glm::vec3(1.0f, 1.0f, 1.0f), 10.f, 0.3f);
    trans1->setNu(1.0f);
    trans1->setDmax(8.0f);

    shared_ptr<Transparent> trans2 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.8f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.3f);
    trans2->setNu(1.0f);
    trans2->setDmax(8.0f);

    shared_ptr<Transparent> trans3 = make_shared<Transparent>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.2f, 0.2f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 0.2f);
    trans3->setNu(1.5f);
    trans3->setDmax(8.0f);

    shared_ptr<Metal> met1 = make_shared<Metal>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.90f, 0.76f, 0.46f), glm::vec3(0.7f, 0.7f, 0.7f), 10.0f, 1.0f);

    shared_ptr<Lambertian> lam1 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.8f, 0.8f, 0.8f), glm::vec3(1.0f, 1.0f, 1.0f), 10.f, 1.f);

    shared_ptr<Metal> met2 = make_shared<Metal>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.65f, 0.77f, 0.97f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 0.5f);

    shared_ptr<Lambertian> lam3 = make_shared<Lambertian>(glm::vec3(0.2f, 0.2f, 0.2f), glm::vec3(0.9f, 0.9f, 0.9f), glm::vec3(1.0f, 1.0f, 1.0f), 10.0f, 1.0f);

    objects.clear();
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-1.42f, -0.75f, 0.0f), 0.2f, trans1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-0.95f, -0.77f, 0.0f), 0.2f, trans2));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-0.48f, -0.79f, 0.0f), 0.2f, trans3));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.03f, -0.80f, 0.0f), 0.2f, met1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.52f, -0.82f, 0.0f), 0.2f, lam1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.99f, -0.84f, 0.0f), 0.2f, met2));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(1.48f, -0.83f, 0.0f), 0.2f, lam3));
    /******************
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-1.42f, -0.75f, -30.0f), 0.2f, met1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-0.95f, -0.77f, -30.0f), 0.2f, lam1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(-0.48f, -0.79f, -30.0f), 0.2f, trans3));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.03f, -0.80f, -30.0f), 0.2f, trans1));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.52f, -0.82f, -30.0f), 0.2f, trans2));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.99f, -0.84f, -30.0f), 0.2f, lam3));
    objects.push_back(std::make_shared<Sphere>(glm::vec3(1.48f, -0.83f, -30.0f), 0.2f, met2)); */
}

void Scene::init()
{
    // Inicialitzem la llista d'objectes
    test4();

    // Llum global
    lightGlobal = vec3(0.1f, 0.1f, 0.1f);

    // Un cop hem afegit les figures, calculem la bounding box
    buildBoundingBox();
}

void Scene::generateRandomSpheres(int numSpheres)
{
    // Pots modificar aquest mètode com vulguis per a generar les esferes que vulguis
    objects.clear(); // Neteja els objectes anteriors

    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<float> posDist(-5.0f, 5.0f); // Distribució per posició
    std::uniform_real_distribution<float> radDist(0.5f, 1.5f);  // Distribució per radi
    std::uniform_real_distribution<float> colDist(0.0f, 1.0f);  // Distribució per color

    for (int i = 0; i < numSpheres; ++i)
    {
        glm::vec3 center(posDist(gen), posDist(gen), posDist(gen) - 4.0f);
        float radius = radDist(gen);
        glm::vec3 color(colDist(gen), colDist(gen), colDist(gen));

        objects.push_back(std::make_shared<Sphere>(center, radius, color));
    }
    objects.push_back(std::make_shared<Sphere>(glm::vec3(0.0f, -100.5f, -5.0f), 95.0f, glm::vec3(0.8f, 0.8f, 0.0f))); // Esfera gran

    // Un cop hem afegit les esferes, calculem la bounding box
    buildBoundingBox();
}

void Scene::generateProceduralAnimation(int numFrames)
{
    float tx = 0.1f;
    float ty = 0.05f;
    float tz = 0.0f;
    for (auto o : objects)
    {
        glm::vec3 t = glm::vec3(tx, ty, tz);
        auto animation = make_shared<Animation>();
        animation->frameIni = 0;
        animation->frameFinal = numFrames;
        animation->transf = make_shared<TranslateTG>(t);
        o->addAnimation(animation);
    }

    // TODO: fer les animacions que vulguis amb els teus objectes
}

void Scene::buildBoundingBox()
{
    // Si no hi ha cap objecte, no podem construir la bounding box
    if (objects.empty())
        return;

    // Inicialitzem amb valors elevats
    vec3 minBounds(FLT_MAX, FLT_MAX, FLT_MAX);
    vec3 maxBounds(-FLT_MAX, -FLT_MAX, -FLT_MAX);

    for (unsigned int i = 0; i < objects.size(); i++)
    {
        const shared_ptr<Box> &objectBox = objects[i]->getBoundingBox();

        if (objectBox)
        {
            // Obtenim els vertexs del bounding box
            vector<vec3> vertexs = objectBox->getVertexs();

            for (unsigned int j = 0; j < vertexs.size(); j++)
            {
                // Obtenim el vertex actual i actualitzem els limits dels bounds
                vec3 vertex = vertexs[j];

                minBounds.x = std::min(minBounds.x, vertex.x);
                minBounds.y = std::min(minBounds.y, vertex.y);
                minBounds.z = std::min(minBounds.z, vertex.z);

                maxBounds.x = std::max(maxBounds.x, vertex.x);
                maxBounds.y = std::max(maxBounds.y, vertex.y);
                maxBounds.z = std::max(maxBounds.z, vertex.z);
            }
        }
    }

    // Creem la bounding box final amb els min/max calculats
    vector<vec3> vertexs = {minBounds, maxBounds};

    // Guardem la bounding box final
    boundingBox = std::make_shared<Box>(vertexs);
}