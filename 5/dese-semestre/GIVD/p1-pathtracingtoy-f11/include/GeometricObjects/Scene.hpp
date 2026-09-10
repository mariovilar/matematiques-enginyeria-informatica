#pragma once

#include "GeometricObjects/Object.hpp"
#include "GeometricObjects/Sphere.hpp"
#include "GeometricObjects/Plane.hpp"
#include "GeometricObjects/Box.hpp"
#include "Utilities/TGs/TranslateTG.hpp"

class Scene : public Hittable, public Animable
{
public:
    // Vector d'objectes continguts a l'escena
    vector<shared_ptr<Object>> objects;

    // Boundry box de la escena
    shared_ptr<Box> boundingBox;

    vec3 lightGlobal;

    Scene();
    virtual ~Scene() {};

    // TODO
    // Funcio que calcula la interseccio del raig r amb l'escena. Guarda la informacio
    // del punt d'interseccio més proper a t_min del Raig, punt que està entre t_min i t_max del Raig.
    // Retorna cert si existeix la interseccio, fals, en cas contrari. A ShadeInfo es retorna la
    // informació de la intersecció, en cas que existeixi.
    virtual bool hit(Ray &r, float tmin, float tmax, ShadeInfo &shadeInfo) const override;

    // TODO
    // Mètode que retorna totes les interseccions que es troben al llarg del raig entre tmin i tmax
    virtual bool allHits(Ray &r, float tmin, float tmax, vector<ShadeInfo> &listShadeInfos) const override;

    void update(int nframe) override;
    void aplicaTG(shared_ptr<TG> tg) override;

    void addObject(shared_ptr<Object> obj) { objects.push_back(obj); };

    void clear() { objects.clear(); };

    void generateRandomSpheres(int numSpheres);

    void init();

    void generateProceduralAnimation(int numFrames);

    void buildBoundingBox();

private:
    void test1();
    void test2();
    void test3();
    void test4();
    void test5();
    void finalImage();
};
