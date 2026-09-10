# Pràctica 1: Raytracing

Pràctica 1 - GiVD 2024-25

En aquest fitxer cal que feu l'informe de la pràctica 1. Aneu omplint la informació que us demanem.

## Equip:

**Equip:** Posar aqui la **lletra i numero de l'equip** al que pertanyeu

* David Díez
* Mario Vilar (mariovilar)

**Nota:** Per cada estudiant cal indicar el nom complert i l'usuari de GitHub entre parèntesis.

## Informe de progrés

Cal que aneu indicant a cada sessió les tasques que heu realitzat i les que no, donant resposta a les preguntes que es formulen en l'enunciat. Com a exemple, us donem l'esquelet inicial a partir de l'enunciat de la primera etapa, tant de **features** implementades com de preguntes a contestar:


### Features (marqueu les que heu fet i qui les ha fet)

- Introducció: Primera etapa
    - [x] Mostrar color del Background
      - David Díez i Mario Vilar
    - [x] Mostrar textura al Background
      - David Díez i Mario Vilar
    - [x] Color Shading
      - David Díez i Mario Vilar
    - [x] Intersecció amb l'escena (1 esfera)
      - Mario Vilar
    - [x] Normal Shading
      - David Díez
    - [x] Diffuse Shading
      - David Díez
    - [x] Intersecció amb l'escena (múltiples esferes)
      - David Díez
    - [x] Ombres
      - David Díez
    - [x] [**Opcional**] Cel Shading
      - Mario Vilar
    - [x] Hit Box
      - Mario Vilar
    - [-] [**Opcional**] Hit Triangle
      - 


### Preguntes

**3.1 Començant a provar el codi**
  
- **3.1.c: Quina escena es crea? Qui la crea? Quin setup o configuració té la classe? Des d'on es crea?**
A l'AppTracerMain creeem un objecte GUITracerToy i al bucle del main cridem al mètode renderConfigGUI que té aquest objecte. Aquí, obtenim la instància del Controller (recordem que és un Singleton) i accedim al mètode requestRender en funció de si necessitem actualitzar el render, mètode el qual l'únic que fa és posar la variable `needsToRender` a `true`. Passem a la següent iteració del main, on arribem a `Controller::getInstance()->needsToRender()` (línia 89). Allà, si la variable `needsToRender` està a `true` (que ho està per la iteració anterior), executem `world->renderWorld()`.

1. Quina escena es crea?
> Es crea una escena per defecte amb una càmera i una escena buida.
2. Quants objectes té l'escena?
> Inicialment, l'escena no té objectes. Els objectes es poden afegir posteriorment mitjançant el mètode generateRandomSpheres o altres mètodes.
3. Qui la crea?
> L'escena és creada pel constructor de la classe World.
4. Quin setup o configuració té la classe World?
> La configuració de la classe World es defineix mitjançant un objecte de la classe Config, que es passa al mètode renderWorld des del mètode `setConfig`.
5. Des d’on es crea o es refresca?
> La configuració es refresca des del mètode setConfig de la classe World, que es crida dins del mètode renderWorld. Aquesta funció només està definida al `hpp` i guarda la configuració com a atribut i crida a `refreshProperties` de la mateixa classe World.

- **3.1.d: Des d'on es crea el degradat que es veu a la finestra de rendering? Per què es veu aquest degradat? A quin mètode es calcula aquest color?**
> El degradat que es veu a la finestra de rendering es crea en el mètode renderWorld de la classe World. Aquest degradat es veu perquè el color de cada píxel es calcula en funció de la direcció del raig generat per la càmera.
> El color es calcula en el mètode `tracer` de la classe Raycasting, que és una subclasse de Render. Aquest mètode es crida dins del bucle de renderització en renderWorld.
> El mètode tracer de la classe Raycasting és on es calcula el color del píxel basat en la direcció del raig.


- **3.1.e: Com s'ha de modificar el codi per veure el color de background entrat per la interfície en comptes del degradat?**
> Per veure el color de background enrat per la interfície, el programa té un `if`al métode renderConfigGUI() dintre de `GUITracerToy.cpp` on es mostra es dialog per seleccionar la textura o el color. En cas de modificar algun dels dos, la variable `update` s'actualitza i, a la següent iteratció, realitza el canvi quan es crida a renderWorld al fitxer `World.cpp`:
```c++
glm::vec3 pixelColor(0.0f);
if(config->backgroundMode == 0) {
    // En aquest cas, el color del pixel és el color del raig
    pixelColor = config->backgroundColor;
} else if(config->backgroundMode == 1) {
    // En aquest cas, el color del pixel és el color de la llum ambient
    pixelColor = render->tracer(ray);
}
```

- **3.1.f: Si ara volguessis veure una textura de fons, com penses que la podries veure? Podries usar la informació de la direcció del raig per poder obtenir una primera aproximació d’accés a la textura de fons?**
Al propi fitxer `World.cpp`: tan sols hem de modificar una miqueta la condició de l'apartat anterior i obtenim:
```c++
if(config->backgroundMode == 0) {
    // En aquest cas, el color del pixel és el color del raig
    pixelColor = config->backgroundColor;
} 

else if(config->backgroundMode == 1) {
    // En aquest cas, el color del pixel és el color de la llum ambient
    pixelColor = render->tracer(ray);
    // Si hi ha textura de fons
    if(config->background != nullptr) {
        // En aquest cas, el color del pixel és el color de la textura
        float w = (float) config->background->getWidth();
        float h = (float) config->background->getHeight();
        float a = (x * w) / config->viewportWidth;
        float b = (y * h) / config->viewportHeight;
        // Inverteix la coordenada Y perquè la imatge es carrega al revés
        pixelColor = config->background->getPixelColor(a, h-b);
    }
}
```

Cal fixar-se com hem hagut de normalitzar i adapatr-la a la mida de la finestra ja que sino no funcionarà bé.

- **3.1.g: Quants objectes conté la teva escena inicialment? Per això hauràs de cridar al mètode hit de la classe Scene, el podries modificar per a tenir en compte l’esfera? Caldrà que modifiquis també el mètode tracer de la classe Raycasting? Potser podries cridar al mètode hit de la classe Scene?**
> Inicialment la nostra escena conté 4 objectes, que s'inicialitzen a l'inicialitzador de l'escena. Són 4 esferes que es trobaran en una llista d'objectes.
> Prenem únicament la primera esfera, i per mostrar-la modifiquem el mètode hit de la classe Scene. 
```c++
bool Scene::hit(Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const {
  if (objects.empty()) {
        return false;
    }

    // Considerem només la primera esfera
    return objects[0]->hit(r, tmin, tmax, shadeInfo);
}
```
Modificar el mètode tracer de la classe Raycasting no serà estrictament necessari, ja que tot el que té a veure amb `pixelColor` ho hem tractat des de la classe Scene i, efectivament, cridem al mètode hit de la classe Scene desde `World.cpp` i ens queda
```c++
// Mostrem la primera esfera
ShadeInfo shadeInfo = ShadeInfo();
if (scene->hit(ray, 0.001f, FLT_MAX, shadeInfo)) {
    // En aquest cas, el color del pixel és el del objecte
    pixelColor = render->tracer(ray);
}
```

- **3.1.h: Com hi pots accedir? A quina variable pots aconseguir l’estratègia a instanciar? I des d’on la crearàs?**
> Primer de tot veiem que l'estratègia de Menu Shadings s'obté a `GuiTracerToy.cpp` dintre del mètode `renderMenus()` i es guarda a la variable `setup->selectedShader`. 
```c++
int actualSelectedShader = setup->selectedShader;
```
Un cop tenim seleccionada la estratègia, la podem crear al mateix fitxer `World.cpp`:
```c++
// Obtenim el shading strategy
ShadingFactory ShadingFactory = ShadingFactory::getInstance();
auto shadingStrategy = ShadingFactory.createShading((ShadingFactory::SHADING_TYPES) setup->selectedShader, scene, lights, camera->position, true);
```
per tal d'obtenir una instància de l'estratègia que volem fer servir. Finalment, hem de canviar la condició de l'apartat anterior per tenir
```c++
// Mostrem la primera esfera
ShadeInfo shadeInfo = ShadeInfo();
if (scene->hit(ray, 0.001f, FLT_MAX, shadeInfo)) {
    // En aquest cas, el color del pixel és el del objecte
    pixelColor = shadingStrategy->shading(shadeInfo);
}
```

- **3.1.i: Com canviaràs el codi del mètode de shading de la classe NormalShading?**
> Hem de canviar la funció `shading()` de la classe `NormalShading` per tal d'agafar el vector normal a cada punt. Ens queda doncs
```c++
// Funció per calcular la il·luminació amb un únic punt d'intersecció amb el raig
vec3 NormalShading::shading(ShadeInfo &info) {
    // Normalitzem la normal del punt d'intersecció
    vec3 n = normalize(info.normal);
    glm::vec3  pixelColor = 0.5f * (n + vec3(1.0f));
    return pixelColor;
}
```

- **3.1.j: Crea la classe DiffuseShading per tal d'aplicar la il·luminació en funció de la llum. Què has de canviar?**
> Crearem una nova classe `DiffuseShading` que hereta de la classe `ShadingStrategy`. Aquesta classe implementarà la funció `shading()` per calcular el color a cada punt, que en aquest cas dependrà del cosinus de l’angle que forma la normal
i el vector de llum.
> Per tal de poder implementar correctament la nova classe hem hagut de canviar `ShadingFactory.hpp` per afegir l'include de la nova estrategia i, a la vegada, hem canviat `ShadingFactory.cpp` per afegir
```c++
shared_ptr<ShadingStrategy> ShadingFactory::createShading(SHADING_TYPES t, shared_ptr<Scene> scene, vector<shared_ptr<Light>> lights, vec3 lookFrom, bool shadow) {
    shared_ptr<ShadingStrategy> s;
    switch(t) {
    case NORMALSHADING:
        s= make_shared<NormalShading>(scene, lights, lookFrom, shadow);
        break;
    case DIFFUSESHADING:
        s= make_shared<DiffuseShading>(scene, lights, lookFrom, shadow);
        break;
    default:
        s = nullptr;
    }
    return s;
}
```
ja que, si no, ens sortia un `segmentation fault`. La funció `shading()` d'aquesta nova classe és la següent:
```c++
vec3 DiffuseShading::shading(ShadeInfo &info) {
    // Si no hi ha cap llum, retornem el color difús del material
    if (lights.empty()) {
        return info.mat->getDiffuse(info.uv);
    }
    
    // Obtenim la direcció de la llum des del punt d'intersecció
    // La funció vectorL de la llum ens proporciona el vector des del punt a la font de llum.
    vec3 lightDir = normalize(lights[0]->vectorL(info.p));
    
    // Normalitzem la normal del punt d'intersecció
    vec3 normal = normalize(info.normal);
    
    // Calculem el cosinus de l'angle entre la normal i la direcció de la llum
    float cosTheta = glm::max(dot(normal, lightDir), 0.0f);
    
    // Calcula el color difús
    vec3 diffuseColor = info.mat->getDiffuse(info.uv) * cosTheta * lights[0]->getId();

    return diffuseColor;
}
```
on la idea és agafar el vector de la llum i el vector normal a un punt, ambós vectors normalitzats. Calculem el producte escalar entre ells i calculem la component difusa per aplicar les ombres.

Hi ha un problema, però, que quan s'actualitza el mode l'estrategia de mostrar la llum (entre `NormalShading` i `DiffuseShading`) la imatge no s'actualitza fins que hem canviat algun dels paràmetres de càmara o llum. Això és perquè a la funció `renderMenus()` del fitxer `GuiTracerToy.cpp` no s'actualitza la pantalla. Per solucinar això fem
```c++
int actualSelectedShader = setup->selectedShader;

if (ImGui::MenuItem("Normal", NULL, selectedShading == "Normal Shading")) {
    setup->selectedShader = 0;
}
if (ImGui::MenuItem("Diffuse", NULL, selectedShading == "Diffuse")) {
    setup->selectedShader = 1;
}
if (ImGui::MenuItem("Blinn-Phong", NULL, selectedShading == "Blinn-Phong")) {
    setup->selectedShader = 2;
}

if (actualSelectedShader!= setup->selectedShader) {
    Controller::getInstance()->requestRender();
}
```
on bàsicament guardem el valor actual i mirem si és diferent del nou. En cas afirmatiu, apliquem el canvi.

També podem veure que per defecte s'utilitza "Normal Shading". Ens agradaria utilitzar "Diffuse" per veure les imatges amb millors colors. Per fer això cal canviar aquesta línia a `GuiTracerToy.cpp`
```c++
std::string selectedShading = "Diffuse";
```
i al mètode `Config` a `Config.hpp` escriure
```c++
selectedShader = 1;
```

- **3.2.a: Codifica el mètode `hit()`**
> Al fitxer `Scene.cpp` tenim la funció `hit()` que hem de modficar per tal de poder visualitzar correctament els objectes depenent de la seva profunditat (que modelem amb `tmin` i `tmax`). El codi és
```c++
bool Scene::hit(Ray& r, float tmin, float tmax, ShadeInfo &shadeInfo) const {
    // Si no hi ha cap objecte retornem false
    if (objects.empty()) {
        return false;
    }

    bool hit = false;

    // Iterem per a cada objecte en la escena
    for (size_t i = 0; i < objects.size(); ++i) {
        if (objects[i]->hit(r, tmin, tmax, shadeInfo)) {
            hit = true;
            tmax = shadeInfo.t;
        }
    }
    return hit;
}
```
on iterem per tots els objectes que hi ha a la escena i cridem al seu métode `hit()` per veure si hi ha intersecció. Anem actualitzant les variables a mesura que avancem al bucle for.

- **3.2.b: Connecta la interfície gràfica amb el métode que genera les esferes de forma aleatoria**
> Per connectar la interfície gràfica amb el generador de esferes aleatories, hem de canviar una petita part de `GuiTracerToy.cpp` on tenim
```c++
// SECCIÓ ESCENA
if (ImGui::CollapsingHeader("Escena", ImGuiTreeNodeFlags_DefaultOpen)) {
    ImGui::Separator();
    if (ImGui::SliderInt("Nombre d'Esferes", &setup->numSpheres, 1, 20)) {
        // Cridem a la funció per generar noves esferes aleatòries
        Controller::getInstance()->generateRandomSpheres(setup->numSpheres);

        // Refrescem la pantalla per veure els canvis
        Controller::getInstance()->requestRender();
    }
}
```
D'aquesta manera quan actualitzem el valor de "Nombre d'Esferes" a la interfície tindrem els canvis.

- **3.3.a: Actualitza el codi per saber mirar com activar les ombres**
> Per poder activar les ombres ho fem desde `GuiTracerToy.cpp`, en particular, al final de la funció `renderConfigGUI()`. La linea de codi és
```c++
bool actualShadow = setup->shadowMode;
ImGui::Checkbox("Ombres", &setup->shadowMode);

if (actualShadow != setup->shadowMode) {
    Controller::getInstance()->requestRender();
}
```
on hem aplicat una lògica similar a la de `renderMenus()` per tal de refrescar la pàgina en cas de tenir un canvi.

Veiem doncs, que és necessari crear l'atribut `shadowMode` a `Config.hpp`. Ens queda
```c++
bool shadowMode; // false = no shadows, true = shadows
```
i al mètode `Config` definim
```c++
shadowMode = false;
```

- **3.3.b: Implementa el raig d'ombra**
> El primer que hem de fer és descomentar la linea `float computeShadow(vec3 point);` de `ShadingStrategy.hpp` i fer la seva implementació. Per això cal crear `ShadingStrategy.cpp` amb la funció corresponent.

``` c++
float ShadingStrategy::computeShadow(vec3 point) {
    // Si les ombres no estan activades o no hi ha llum, no hi ha ombra
    if (!shadow || lights.empty()){
        return false;
    }
    
    // Calculem la direcció de la llum des del punt
    vec3 lightDir = normalize(lights[0]->vectorL(point));

    // Obtenim la distància des del punt fins a la llum
    float distanceToLight = lights[0]->distanceToLight(point);
    
    // Creem el raig d'ombra
    Ray shadowRay(point, lightDir);
    ShadeInfo shadowInfo;
    
    // Si hi ha alguna intersecció entre el punt i la llum, el punt està a l'ombra
    return scene->hit(shadowRay, 0.001f, distanceToLight, shadowInfo);
}
```

i a la funció `shading()` de l'arxiu `NormalShading` tan sols cal afegir una condició per configurar les ombres
``` c++
// Funció per calcular la il·luminació amb un únic punt d'intersecció amb el raig
vec3 NormalShading::shading(ShadeInfo &info) {
    // Comprovem si el punt d'intersecció està a l'ombra
    if (computeShadow(info.p)) {
        return info.mat->Ka * lights[0]->getIa();
    }

    // Normalitzem la normal del punt d'intersecció
    vec3 n = normalize(info.normal);
    glm::vec3  pixelColor = 0.5f * (n + vec3(1.0f));
    return pixelColor;
}
```
- **3.3.c: És eficient fer-ho d’aquesta manera? Com podries pensar d’accelerar el procés? Potser ara vas a 0.2FPS, com podríem reduir el temps de càlcul?**



- **4.1: T’animes a afegir ombres al DiffuseShading?**
Afegir-les és molt fàcil, solament cal tenir en compte el mètode `computeShadow()` creat anteriorment:
```c++
if (computeShadow(info.p)) {
    return vec3(0.0f); // El punt està a l'ombra
}
```
La tria del vector podria ser millorada, però de moment l'enunciat no especifica, pel que ho deixarem així.

- **4.2: Implementa un nou shading, l’anomenat Cel Shading per a aconseguir visualitzacions semblants a les dels dibuixos animats. Necessites afegir informació en el material?**
No té misteri una vegada hem seguit el DiffuseShading (el que m'ha servit de referència per implementar-lo ràpidament). El més rellevant, doncs, és com implementem el `shading()`. La implementació naïf que hem seguit ha estat la següent:
```c++
// Retornem el color quantitzant el valor del cosinus de l'angle
float levels = 4.0f; // Number of quantization levels
cosTheta = floor(cosTheta * levels) / levels;
// Apply the quantized cosTheta to the diffuse color
diffuseColor *= cosTheta;
```
Pot ser que usant els atributs existents a material puguem millorar el resultat. De moment, ho deixarem així.

- **4.3:  Crea una nova classe Box per definir una capsa 3D definida pels seus vèrtexs extrems, suposant que la capsa estarà alineada als eixos coordinats.**
La creació de la nova classe no té molt misteri. Ens centrem en la implementació del mètode `hit()`. Està dissenyada per determinar si un `Ray` donat interseca amb la `Box`. La funció pren quatre paràmetres:
1. `Ray& r`: Una referència a un objecte `Ray`. Això representa el raig que s'està provant per a la intersecció amb la caixa.
2. `float tmin`: Un valor de punt flotant que representa el valor mínim vàlid del paràmetre al llarg de la direcció del raig. Això s'utilitza per limitar el rang del raig per a la prova d'intersecció.
3. `float tmax`: Un valor de punt flotant que representa el valor màxim vàlid del paràmetre al llarg de la direcció del raig. Això limita encara més el rang del raig per a la prova d'intersecció.
4. `ShadeInfo &shadeInfo`: Una referència a un objecte `ShadeInfo`. Aquesta estructura s'utilitza típicament per emmagatzemar informació sobre el punt d'intersecció, com ara el punt d'impacte, la normal a la intersecció i les propietats del material. Aquesta informació és crucial per als càlculs d'ombratge després de detectar una intersecció.

La funció retorna un valor booleà (`bool`). Si la funció retorna `true`, indica que el raig interseca la caixa dins del rang especificat (`tmin` a `tmax`). Si retorna `false`, significa que no hi ha intersecció dins d'aquest rang.

- **4.4: Implementar el mètode de hit en objectes com malles triangulars o Mesh**

### Explicació de la pràctica    
  * **Organització de la pràctica**
    * Està explicat al comentari del principi.
  
  * **Decisions a destacar**
    * Comenteu les decisions que heu pres 


### Screenshots

Per tal de documentar els vostres resultats, és important que adjunteu imatges amb el resultat i quan convingui, captures de pantalla amb la configuració que el genera. Per totes les imatges cal incloure un peu d'imatge indicant què es mostra, i tota la informació necessària per reproduïr l'imatge (la configuració, els objectes i les seves propietats, llums, ...)

## Fitxa 2: RayTracing
Respondrem una sel·lecció de les preguntes que considerem més interessants.
- **1.2. Quan usaràs la capsa per optimitzar les teves interseccions amb el raig?** 
> Bàsicament el que ens permet la caixa és descartar interseccions més ràpid. Si un raig no interseca amb la caixa, evidentment no intersecarà amb l'objecte inscrit en la caixa. És una fita bastant barroera (en el sentit de poc precisa), però que ens serveix per millorar de 0.5FPS a 1.5-2FPS, depèn de si ens trobem al dispositiu del David o al meu.

- **1.3. Com canvia la teva eficiència? Si canvies el FOV, notes algun canvi?**
> Com acabem de dir, la Box és especialment útil en escenes amb molts objectes, ja que permet descartar ràpidament objectes que no poden ser intersecats pel raig. 
> Per construcció, a més, la caixa ha d'estar alineada amb els eixos; això fa que els càlculs d'interseccions siguin més ràpids i, per tant, és més eficient que calcular interseccions amb formes més complexes com malles detallades.
> El FOV afecta la perspectiva de la càmera i, per tant, la manera com els raigs es projecten a l'escena. Reduir el FPV farà que la càmera cobreixi una àrea més petita de l'escena. Això pot reduir el nombre d'objectes visibles i, per tant, el nombre de càlculs d'intersecció necessaris.

- **3.1. Fes una nova classe derivada de Material que codifiqui el tipus Metal. Inspirat en la classe Lambertian però aquest cop el mètode evaluate() haurà de retornar cert i simularà la reflexió especular.**
> L'únic que farem és destacar la implementació del mètode `evaluate()`. 
> Primer, determina el punt d'impacte del raig. Després, calcula la direcció del raig reflectit utilitzant la normal de la superfície. Si la superfície té rugositat, afegeix una petita variació a la direcció reflectida. Finalment, crea un nou raig reflectit i assigna el color especular a la variable color.

- **3.2. Fes un nou render anomenat Raytracing (pots copiar el teu actual Raycasting a aquesta nova classe i modificar-la). Activa’l des de la interfície gràfica. Revisa el mètode menús() de la classe GUITracerToy per activar l’opció. Adequa el codi del mètode render de la classe Raytracing perquè consideri rajos secundaris i controli la recursivitat amb el valor MAXDEPTH. Afegeix recursió en el raig perquè es segueixin els rajos secundaris REFLECTITS en el cas que l’objecte intersecat sigui un mirall (tingui un material de tipus Metall).**
> El mètode tracerRecursiu és una funció recursiva que determina el color d'un píxel traçant el camí d'un raig a través de l'escena. Inicialitza el color a negre, comprova interseccions amb objectes i la caixa englobant, i calcula el color mitjançant ombrejat i raigs secundaris fins a una profunditat màxima. Si no hi ha intersecció, utilitza el color de fons. Finalment, clampa el color per assegurar-se que està dins del rang vàlid [0, 1]. El més fonamental del codi és la següent condició:
```c++
if (shouldTestScene && scene->hit(ray, 0.001f, FLT_MAX, *shadeInfo)) {        
    // Si alcanzamos la profundidad máxima, agafem el color del píxel que trobi el raig
    if(depth >= config->MAXDEPTH) {
        return pixelColor;
    } 
    else {
        // Cridem a evaluate() del material per calcular el raig secundari
        Ray scatteredRay;
        vec3 scatteredColor;
        vec3 newColor = shadingStrategy->shading(*shadeInfo);
        if (shadeInfo->mat && shadeInfo->mat->evaluate(ray, shadeInfo->t, shadeInfo->normal, scatteredColor, scatteredRay)) {
            // Si evaluate retorna true, calculem la contribució recursiva del raig secundari
            newColor += (scatteredColor * tracerRecursiu(scatteredRay, depth + 1));
        }
        pixelColor += newColor;
    }
}
```
> Si el raig interseca amb un objecte de l'escena:
> - Profunditat Màxima: Si la profunditat de recursió ha arribat al màxim permès (config->MAXDEPTH), es retorna el color actual del píxel.
> - Ombrejat i Raigs Secundaris: En cas contrari, es calcula la contribució del color mitjançant l'estratègia d'ombrejat del material. Si el mètode evaluate del material retorna cert, es traça recursivament el raig secundari i s'afegeix la seva contribució al color del píxel.

- **3.7. Si treus l’esfera gran, pots comprovar quins efectes té utilitzar o no la Bounding Box de l’escena quan augmentes la profunditat del raig?**
> Sí, hem posat una condició de tal manera que puguem optimitzar el Raytracing amb bounding boxes, cosa que permet reduir el temps de càlcul lleugerament.
```c++
// Si hi ha bounding box pero el raig no interseca, retornem false
bool shouldTestScene = !config->boundingBox || (scene->boundingBox && scene->boundingBox->hit(ray, 0.001f, FLT_MAX, *shadeInfo));
// Si s'ha intersecat amb la bounding box o no s'utilitza, comprova si hi ha intersecció amb l'escena
if (shouldTestScene && scene->hit(ray, 0.001f, FLT_MAX, *shadeInfo))
```

- **3.8. Què passa si quan no hi ha hit retornes el color de fons o de textura. Com et queden les teves escenes?**
Aquesta casuística també l'hem tingut en compte i és la d'aquesta condició.
```c++
bool paintBackground = config->depthShading || (!config->depthShading && depth == 0);
if(paintBackground) {
    // Si hi ha textura de fons
    if(config->backgroundMode == 1 && config->background) {
        float w = (float)config->background->getWidth();
        float h = (float)config->background->getHeight();
        float a = (ray.direction.x + 1) * 0.5 * w;
        float b = (ray.direction.y + 1) * 0.5 * h;
        // Inverteix la coordenada Y perquè la imatge es carrega al revés
        pixelColor += config->background->getPixelColor(a, b);
    }
    else {
        pixelColor += config->backgroundColor;
    }
}
else {
    pixelColor += scene->lightGlobal;
}
```
Hem creat un flag al config de manera que puguem controlar aquest cas quan ho necessitem o quan se'ns demani.

## Fitxa 3: Transparències
### Primera part
La creació del nou material és bastant rutinària i no entrarem en detalls. La modificació que hem fet al `Raytracing` és bastant mínima:
```c++
else {
    // Cridem a evaluate() del material per calcular el raig secundari
    Ray scatteredRay;
    vec3 scatteredColor;
    // New color
    vec3 newColor = shadingStrategy->shading(*shadeInfo);

    if (shadeInfo->mat && shadeInfo->mat->evaluate(ray, shadeInfo->t, shadeInfo->normal, scatteredColor, scatteredRay)) {
        // Transparència
        newColor *= (1.f - shadeInfo->mat->Kt);
        // Si evaluate retorna true, calculem la contribució recursiva del raig secundari
        newColor += (scatteredColor * tracerRecursiu(scatteredRay, depth + 1));
    }
    pixelColor += newColor;
}
``` 
El gruix de codi el tenim a l'`evaluate` del nou `Material`:
```c++
float Transparent::reflectionRatio(vec3 I, vec3& normal) const {
    float reflection_ratio;
    if (dot(I, normal) > 0) {
        // Ray exiting transparent material
        normal = -normal;
        reflection_ratio = nut;
    }
    else {
        // Ray entering transparent material
        reflection_ratio = 1/nut;
    }
    return reflection_ratio;
}
bool Transparent::evaluate(const Ray &r_in, float t, vec3 n, vec3 &color, Ray &r_out) const
{
    vec3 I = glm::normalize(r_in.direction);
    vec3 normal = glm::normalize(n);
    vec3 rec = r_in(t) + FLT_EPSILON * I;

    float ratio = reflectionRatio(I, normal);
    vec3 refracted = glm::refract(I, normal, ratio);

    if (length(refracted) < FLT_EPSILON) {
        // Total internal reflection
        vec3 reflected = glm::normalize(glm::reflect(I, normal));
        r_out = Ray(rec, reflected);
        color = Ks; // Reflected color
    } else {
        // Ray is transmitted
        refracted = glm::normalize(refracted);
        r_out = Ray(rec, refracted);
        color = Kt; // Transmitted color
    }
    return true;
}
```
Hem perdut molt temps perquè no s'havia especificat clarament que `Kt` estava fixat a `1.0f`, estava a l'últim test de tots de la primera part. Això ha fet que ens haguem enredat innecessàriament amb el codi i no haguem pogut veure les transparències fins adonar-nos d'aquest error a l'enunciat. Hem vist aquesta línia a posteriori d'haver fet les proves, així que cal notar que nosaltres les hem fet a `Kt=0.5f`. Alguns dels testos ens han donat les següents imatges:
![Primera imatge](./resources/testos/Screenshot%202025-03-11%20at%2014.16.00.png)
![Segona imatge](./resources/testos/Screenshot%202025-03-11%20at%2014.18.21.png)
![Tercera imatge](./resources/testos/Screenshot%202025-03-11%20at%2014.18.35.png)
![Quarta imatge](./resources/testos/Screenshot%202025-03-11%20at%2014.29.28.png)
![Cinquena imatge](./resources/testos/Screenshot%202025-03-11%20at%2018.38.23.png)

**3.9. Si assignes el color ambient global enlloc del de background en els rajos secundaris que no intersequen amb res. Com et canvia la visualització? Raona el per què?**
En aquest cas, en lloc de no veure la bola transparent a partir de profunditat 2 (com passa amb un objecte amb `nu_t=1`, al ser completament transparent), es veu un semicercle negre allà on hauria d'haver-hi el background blanc. Si desactivem i activem el flag veiem que el responsable d'aquest color és la llum global, que està configurada a `vec3(0.1f)`. Si en aquella condició assignem `vec3(1.f)`, veurem com clarament aquest semicercle passa a blanc.

**3.10. Fins ara la Kt està a 1.0, prova de canviar-la a 1.5. Quins efectes es veuen a l’esfera transparent? Per què?**
Aquesta pregunta torna a estar mal formulada, ja que es refereix a la `nu_t` del material. Efectivament, no té cap sentit un factor `1-Kt` negatiu. Si fem aquest canvi, ens surt el resultat esperat. Això és perquè com més gran sigui l'índex de refracció, més es desviaran els rajos de llum quan entrin o surtin del material. La refracció afecta la direcció i la intensitat dels rajos de llum.