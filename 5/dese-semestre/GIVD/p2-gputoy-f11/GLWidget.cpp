#include "GLWidget.hpp"

GLWidget::GLWidget(int w, int h) : world(nullptr), xRot(0.0f), yRot(0.0f), zRot(0.0f),
                                   xTras(0.0f), yTras(0.0f),
                                   shaderColor(0), shaderTexture(0), program(0),
                                   mousePressed(false), lastMouseX(0.0), lastMouseY(0.0)
{
    // inicialització de la configuració
    config = GPUConfig(w, h);
    transform = glm::mat4(1.0f);
}

GLWidget::~GLWidget()
{
}

void GLWidget::initializeGL()
{
    // inicialitzacions OpenGL
    setupOpenGLFeatures();

    // Inicialitzacions dels shaders
    initShadersGPU();

    // Creació dels objectes de l'escena
    initWorld();

    // Activació del shader per defecte i enviament del mon a la GPU
    activateShader("Color", NULL);
}

// Activa les característiques d'OpenGL que es faran servir
void GLWidget::setupOpenGLFeatures()
{
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glEnable(GL_RGBA);
    glEnable(GL_DOUBLE);
}

// Inicialització de la geometria de l'escena i preparació per enviar-la a la GPU
void GLWidget::initWorld()
{
    world = make_shared<GPUWorld>();

    auto lightsManager = make_shared<GPULightsManager>();
    world->setLightManager(lightsManager);

    auto camera = make_shared<GPUCamera>(config.observador, config.vrp, config.vup, config.fov, config.zNear, config.zFar,
                                         config.viewportWidth, config.viewportHeight);
    world->setCamera(camera);

    // Creació de l'escena
    auto scene = make_shared<GPUScene>();
    world->setScene(scene);
    world->setConfig(make_shared<GPUConfig>(config));
}

void GLWidget::paintGL()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // En el cas d'usar textures, cal reactivar la textura
    if (program->getName() == shaderTexture->getName() && world->isTextured())
    {
        world->rebindTexture();
    }

    // Dibuixar l'escena
    world->draw();
}

void GLWidget::initShadersGPU()
{
    shaderColor = make_shared<GPUShader>("Color", "vTest.glsl", "fTest.glsl");
    shaderTexture = make_shared<GPUShader>("Texture", "vshader2.glsl", "fshader2.glsl");
    shaderMaterial = make_shared<GPUShader>("Material", "vMaterial.glsl", "fMaterial.glsl");
    shaderNormal = make_shared<GPUShader>("Normal", "vNormal.glsl", "fNormal.glsl");
    shaderPhong = make_shared<GPUShader>("Phong", "vPhong.glsl", "fPhong.glsl");
    shaderGouraud = make_shared<GPUShader>("Gouraud", "vGouraud.glsl", "fGouraud.glsl");
    shaderToon = make_shared<GPUShader>("Toon", "vToon.glsl", "fToon.glsl");
    shaderPhongTexture = make_shared<GPUShader>("Phong (Texture)", "vPhongTex.glsl", "fPhongTex.glsl");

    // shaders per defecte
    program = shaderColor;
}

void GLWidget::activateShader(const char *typeShader, const char *nameTexture)
{
    if (std::strcmp(typeShader, "Color") == 0)
    {
        program = shaderColor;
        program->use();
        world->toGPU(program->getId());
    }
    else if (std::strcmp(typeShader, "Material") == 0)
    {
        program = shaderMaterial;
        program->use();
        world->toGPU(program->getId());
    }
    else if (std::strcmp(typeShader, "Normal") == 0)
    {
        program = shaderNormal;
        program->use();
        world->toGPU(program->getId());
    }
    else if (std::strcmp(typeShader, "Phong") == 0)
    {
        program = shaderPhong;
        program->use();
        world->toGPU(program->getId());
    }
    else if(std::strcmp(typeShader, "Gouraud") == 0)
    {
        program = shaderGouraud;
        program->use();
        world->toGPU(program->getId());
    }
    else if (std::strcmp(typeShader, "Toon") == 0)
    {
        program = shaderToon;
        program->use();
        world->toGPU(program->getId());
    }
    else if (std::strcmp(typeShader, "Texture") == 0)
    {
        program = shaderTexture;
        program->use();
        world->toGPUTexture(program->getId());
        if (nameTexture != NULL && nameTexture[0] != '\0')
        {
            world->initTextureGL(nameTexture);
        }
        else
        {
            std::cerr << "ERROR: No hi ha nom de textura." << std::endl;
        }
    }
    else if (std::strcmp(typeShader, "PhonTex") == 0)
    {
        program = shaderPhongTexture;
        program->use();
        world->toGPUTexture(program->getId());
        if (nameTexture != NULL && nameTexture[0] != '\0')
        {
            world->initTextureGL(nameTexture);
        }
        else
        {
            std::cerr << "ERROR: No hi ha nom de textura." << std::endl;
        }
    }
    else
    {
        std::cerr << "Error: Tipus de shader desconegut." << std::endl;
        return;
    }
    // El lloc més adequat per actualitzar la llum ambient global (i altres paràmetres uniformes com les llums)
    // a la GPU és just després d'activar un programa shader
    updateGlobalAmbientLight();
    updateAllLights();
    updateZThreshold();
}

void GLWidget::updateZThreshold()
{
    world->updateZThreshold(program->getId(), config.zThreshold);
}

// Funcions de control del mouse
void GLWidget::mousePressEvent(GLFWwindow *window, int button, int action, int mods)
{
    if (button == GLFW_MOUSE_BUTTON_LEFT)
    {
        if (action == GLFW_PRESS)
        {
            mousePressed = true;
            glfwGetCursorPos(window, &lastMouseX, &lastMouseY);
        }
        else if (action == GLFW_RELEASE)
        {
            mousePressed = false;
        }
    }
}

void GLWidget::buttonPressEvent()
{
    // Si es fa click al checkbox de la GUI
    shared_ptr<TG> t, r;

    // Fem una translació (-1,0,0) i una rotació de 45 graus al voltant de l'eix X
    setXRotation(45.0f);
    setYRotation(0.0f);
    setZRotation(0.0f);
    setXTras(-1.0f);
    setYTras(0.0f);

    // Calculem la rotació i translació de l'objecte
    vec3 rot = vec3(xRot, yRot, zRot);
    r = make_shared<Rotate>(rot);
    vec3 tras = vec3(xTras, yTras, 0.0f);
    t = make_shared<Translate>(tras);

    // Transformació a l'últim objecte (que es faci a nivell d'objecte, que serà el cub)
    world->setTGLastObject(t->getTG() * r->getTG()); // Actualitzar la transformació de l'últim objecte
}

void GLWidget::mouseMoveEvent(GLFWwindow *window, double xpos, double ypos)
{
    if (mousePressed)
    {
        double dx = (xpos - lastMouseX) * config.sensitivityAmount;
        double dy = (ypos - lastMouseY) * config.sensitivityAmount;

        lastMouseX = xpos;
        lastMouseY = ypos;

        shared_ptr<TG> t, r;

        // Update rotation angles based on mouse movement
        if (config.mouseMode == 1)
        {
            float newXRot = xRot + float(dy); // Reduced sensitivity for smoother rotation
            float newYRot = yRot + float(dx); // Reduced sensitivity for smoother rotation

            setXRotation(newXRot);
            setYRotation(newYRot);
        }
        else if (config.mouseMode == 0)
        {
            float newXTras = xTras + float(dx) * translationFactor; // Reduced sensitivity for smoother traslation
            float newYTras = yTras - float(dy) * translationFactor; // Reduced sensitivity for smoother traslation

            setXTras(newXTras);
            setYTras(newYTras);
        }
        
        // Calculem la rotació i translació de l'objecte
        vec3 rot = vec3(xRot, yRot, zRot);
        r = make_shared<Rotate>(rot);
        vec3 tras = vec3(xTras, yTras, 0.0f);
        t = make_shared<Translate>(tras);

        // OBSERVACIÓ: La crida global world->aplicaTG(transform) és el que cal eliminar per permetre transformacions individuals.
        // Transformació a l'últim objecte (que es faci a nivell d'objecte)
        world->setTGLastObject(t->getTG() * r->getTG()); // Actualitzar la transformació de l'últim objecte
    }
}

// Funcions per a la GUI
void GLWidget::setXRotation(float angle)
{
    // Normalize to [0, 360)
    angle = fmod(angle, 360.0f);
    if (angle < 0)
        angle += 360.0f;

    if (fabs(angle - xRot) > 0.01f)
    {
        xRot = angle;
    }
}

void GLWidget::setYRotation(float angle)
{
    // Normalize to [0, 360)
    angle = fmod(angle, 360.0f);
    if (angle < 0)
        angle += 360.0f;

    if (fabs(angle - yRot) > 0.01f)
    {
        yRot = angle;
    }
}

void GLWidget::setZRotation(float angle)
{
    // Normalize to [0, 360)
    angle = fmod(angle, 360.0f);
    if (angle < 0)
        angle += 360.0f;

    if (fabs(angle - zRot) > 0.01f)
    {
        zRot = angle;
    }
}

void GLWidget::setXTras(float tras)
{
    if (fabs(tras - xTras) > 0.01f)
    {
        xTras = tras;
    }
}
void GLWidget::setYTras(float tras)
{
    if (fabs(tras - yTras) > 0.01f)
    {
        yTras = tras;
    }
}

int GLWidget::getRenderMode() const
{
    return config.renderMode;
}

void GLWidget::setRenderMode(int mode)
{
    config.renderMode = mode;
}

void GLWidget::updateCamera()
{
    world->updateCamera(program->getId(), config.observador, config.vrp,
                        config.vup, config.fov, config.zNear, config.zFar);
}

void GLWidget::updateMaterial()
{
    world->updateMaterial(program->getId(), config.ambientColor, config.diffuseColor, config.specularColor, config.shininess);
}

void GLWidget::updateGlobalAmbientLight()
{
    world->updateGlobalAmbientLight(program->getId(), config.lightAmbientGlobal);
}

// Actualitza totes les llums, per què una ha estat activada o desactivada
// o per què s'ha esborrat alguna de les llums
void GLWidget::updateAllLights()
{
    world->updateAllLights(program->getId(), config.lights);
}

// Només han canviat alguna propietat de la llum que està a 'index' de la taula i està activa
void GLWidget::updateSingleLight(int index)
{

    // Update this specific light in the shader
    world->updateSingleLight(program->getId(), config.lights[index], index);
}

void GLWidget::updateBackground()
{

    // Color background
    glClearColor(config.backgroundColor.r, config.backgroundColor.g, config.backgroundColor.b, 1.0f);
}

void GLWidget::loadObject(const char *filename)
{
    // Load object from file
    auto mesh = make_shared<Mesh>(filename);
    mesh->make();

    // Cal afegir Material a l'objecte de forma aleatòria
    // mesh->setMaterial(make_shared<GPUMaterial>(vec3(0.5f, 0.5f, 0.5f), vec3(0.5f, 0.5f, 0.5f), vec3(1.0f, 1.0f, 1.0f), 32.0f));

    // Reiniciar les transformacions *abans* d'afegir i actualitzar
    xRot = yRot = zRot = 0.0f;
    xTras = yTras = 0.0f;

    world->addObject(shared_ptr<Object>(mesh));

    // Cal actualitzar la GPU amb el nou objecte
    world->lastObjectToGPU(program->getId());
}

void GLWidget::updateAdvancedSettings()
{
    if (config.envMap == 1) {
        world->updateAdvancedSettings(config.envMap);
    }

}

void GLWidget::addCube()
{
    auto c = new Cub();
    c->make();

    // Cal afegir Material de forma aleatòria
    // c->setMaterial(make_shared<GPUMaterial>(vec3(0.5f, 0.5f, 0.5f), vec3(0.5f, 0.5f, 0.5f), vec3(1.0f, 1.0f, 1.0f), 32.0f));

    // Reiniciar les transformacions *abans* d'afegir i actualitzar
    xRot = yRot = zRot = 0.0f;
    xTras = yTras = 0.0f;

    world->addObject(shared_ptr<Object>(c));

    // Cal actualitzar la GPU amb el nou objecte
    world->lastObjectToGPU(program->getId());
}

void GLWidget::reset()
{
    // Reset the scene
    world->scene->objects.clear();
    world->toGPU(program->getId());
}