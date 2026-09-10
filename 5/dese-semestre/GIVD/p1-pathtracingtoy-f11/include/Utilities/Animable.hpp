#pragma once

#include <vector>
#include <memory>

#include "glm/glm.hpp"
#include "Utilities/TGs/TG.hpp"

#define MAXFRAMES 20

using namespace std;

class Animation {
public:
    int frameIni;
    int frameFinal;
    shared_ptr<TG> transf;

    Animation(): frameIni(0), frameFinal(5), transf(NULL) {}

    // "operator =" per la classe  Animation
    Animation &operator =(const Animation &rhs) {
    frameIni = rhs.frameIni;
    frameFinal = rhs.frameFinal;
    transf = rhs.transf;
    return *this;
    }
};

class Animable {
public:
    Animable() {};
    ~Animable(){};

    std::vector<shared_ptr<Animation>> animFrames;

    void addAnimation(shared_ptr<Animation> anim) {
        animFrames.push_back(anim);
    };
    
    // Update recorre la llista de frames per detectar quina animació aplicar.
    // Crida a aplicaTG quan l'ha trobada
    virtual void update(int nframe) {
        bool trobat = false;
        int i;
        for (i = 0; i<animFrames[animFrames.size()-1]->frameFinal && !trobat; i++)
        trobat = animFrames[i]->frameFinal>=nframe;

        aplicaTG(animFrames[i-1]->transf);
    };

    // Obliga als objectes que tenen animacions implementar aquest mètode
    virtual void aplicaTG(shared_ptr<TG> tg) = 0;
};
