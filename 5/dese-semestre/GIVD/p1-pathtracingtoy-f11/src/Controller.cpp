#define GL_SILENCE_DEPRECATION

#include "Controller.hpp"

Controller *Controller::instancePtr = nullptr;

Controller *Controller::getInstance()
{
    if (!instancePtr)
    {
        instancePtr = new Controller();
    }
    return instancePtr;
}

Controller::Controller()
{
    // Es creen el word i el setup per defecte
    world = make_shared<World>();
    framebuffer = make_shared<Image>(800, 600, 3);
}

void Controller::refresh()
{
    // Clear del framebuffer d'OpenGL
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawPixels(setup->viewportWidth, setup->viewportHeight, GL_RGB, GL_UNSIGNED_BYTE, framebuffer->getData());
}

// Funció per renderitzar el món virtual
void Controller::renderWorld()
{
    auto start = std::chrono::high_resolution_clock::now(); // ⏱️ Inici del cronòmetre

    if (needsToRender)
    {
        // Crida a la funció que renderitza el món
        world->renderWorld(*framebuffer, setup);
        refresh();
    };

    auto end = std::chrono::high_resolution_clock::now(); // ⏱️ Final del cronòmetre
    std::chrono::duration<float, std::milli> duration = end - start;
    renderTimeMs = duration.count(); // ⏱️ Guardem el temps en ms

    needsToRender = false;
}

void Controller::requestRender()
{
    needsToRender = true; // Activa render només quan es fa un canvi important
}

void Controller::changeCamera(glm::vec3 obs, glm::vec3 vrp, glm::vec3 up, float fov)
{
    world->changeCamera(obs, vrp, up, fov);
}

void Controller::setLightGlobal(vec3 lightGlobal)
{
    world->setLightGlobal(lightGlobal);
}

void Controller::generateRandomSpheres(int numSpheres)
{
    world->generateRandomSpheres(numSpheres);
}

void Controller::setNumSamples(int numSamples)
{
    world->setNumSamples(numSamples);
}

void Controller::saveImage(const char *filename)
{
    framebuffer->saveImage(filename);
}

void Controller::saveAnimation(const char *animationBaseName)
{
    if (animationBaseName != nullptr)
    {
        int numFrames = 4;
        world->generateAnimation(numFrames);
        for (int i = 0; i < numFrames; i++)
        {
            char framePath[256];
            snprintf(framePath, sizeof(framePath), "%s_%03d.png", animationBaseName, i);
            world->update(i);
            world->renderWorld(*framebuffer, setup);
            framebuffer->saveImage(framePath);
            std::cout << "Frame guardat a: " << framePath << std::endl;
        }
    }
}
