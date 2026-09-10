#include "World.hpp"

World::World()
{
    camera = make_shared<Camera>();
    scene = make_shared<Scene>();
}

void World::renderWorld(Image &framebuffer, shared_ptr<Config> config)
{
    // Cridem a la funció per les configuracions
    setConfig(config);

    // Cridem a la funció per guardar les propietats
    refreshProperties();

    // Obtenim el render strategy
    RenderFactory RenderFactory = RenderFactory::getInstance();
    auto render = RenderFactory.createRender((RenderFactory::RENDER_TYPES)config->selectedTracer, scene, lights, camera, config);

    float width = setup->viewportWidth;
    float height = setup->viewportHeight;
    float scale = tan(glm::radians(config->fov) / 2.0f);
    framebuffer.resize(width, height);

    float aspectRatio = static_cast<float>(width) / height;

    // Calcula el vector de direcció des de la càmera al VRP i la resta d'eixos de la càmera
    glm::vec3 cameraDirection = glm::normalize(config->observador - config->vrp);
    glm::vec3 cameraRight = glm::normalize(glm::cross(glm::vec3(0.0f, 1.0f, 0.0f), cameraDirection));
    glm::vec3 cameraUp = glm::cross(cameraDirection, cameraRight);

    for (int y = 0; y < height; y++)
    {
        std::cout << "\r [ " << (int)(100 * (y + 1) / height) << "% ] Rendering in progress " << std::flush;

        for (int x = 0; x < width; x++)
        {
            // Ajusta les coordenades normalitzades de la pantalla
            float u = (2.0f * x) / width - 1.0f;
            float v = 1.0f - 2.0f * y / height;
            float w, h, a, b;

            // Ajusta l'aspect ratio en la coordenada X, suposant que width>height
            // Aplica l'escala del fov
            u = u * scale * aspectRatio;
            v = v * scale;

            // Genera la direcció del raig basant-se en la càmera
            glm::vec3 rayDirection = glm::normalize(-cameraDirection + u * cameraRight + v * cameraUp);
            Ray ray(config->observador, rayDirection);

            // Inicialitzem el color del pixel a tot zeros
            vec3 pixelColor(0.0f);

            // Crida al render per obtenir el color del pixel
            pixelColor = render->tracer(ray);

            // Si tenim un -1 vol dir que no s'ha trobat intersecció, aplicam el mode de fons
            if (pixelColor == vec3(-1.0f))
            {
                if (config->backgroundMode == 0)
                {
                    // En aquest cas, el color del pixel és el color del raig
                    pixelColor = config->backgroundColor;
                }
                else if (config->backgroundMode == 1)
                {
                    // Si hi ha textura de fons
                    if (config->background)
                    {
                        w = (float)config->background->getWidth();
                        h = (float)config->background->getHeight();
                        a = (x * w) / config->viewportWidth;
                        b = (y * h) / config->viewportHeight;

                        // Inverteix la coordenada Y perquè la imatge es carrega al revés
                        pixelColor = config->background->getPixelColor(a, h - b);
                    }
                }
            }

            // Escriu el color al framebuffer
            framebuffer.setPixelColor(x, y, pixelColor);
        }
    }
}

void World::changeCamera(glm::vec3 obs, glm::vec3 vrp, glm::vec3 up, float fov)
{
    camera->position = obs;
    camera->vrp = vrp;
    camera->up = up;
    camera->fov = fov;
}

// Guardem la llum global
void World::setLightGlobal(vec3 lightGlobal)
{
    scene->lightGlobal = lightGlobal;
}

void World::generateRandomSpheres(int numSpheres)
{
    scene->generateRandomSpheres(numSpheres);
}

void World::setNumSamples(int numSamples)
{
    setup->numSamples = numSamples;
}

void World::refreshProperties()
{
    camera->position = setup->observador;
    camera->vrp = setup->vrp;

    if (lights.size() == 0)
    {
        auto light = make_shared<PointLight>(setup->lightPos, setup->lightAmbient, setup->lightDiffuse, setup->lightSpecular);
        lights.push_back(light);
    }
    else
    {
        lights[0]->setIa(setup->lightAmbient);
        lights[0]->setId(setup->lightDiffuse);
        lights[0]->setIs(setup->lightSpecular);
        if (lights[0] == dynamic_pointer_cast<PointLight>(lights[0]))
        {
            dynamic_pointer_cast<PointLight>(lights[0])->setPos(setup->lightPos);
        }
    }
}

void World::generateAnimation(int numFrames)
{
    scene->generateProceduralAnimation(numFrames);
}

void World::update(int nframe)
{
    scene->update(nframe);
}