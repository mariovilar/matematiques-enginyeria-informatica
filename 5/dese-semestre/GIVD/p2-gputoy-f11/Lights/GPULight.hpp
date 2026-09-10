#pragma once
#include <iostream>
#include <string>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "Utilities/GPUConnectable.hpp"
#define MAX_GPU_LIGHTS 8

using namespace glm;
using namespace std;

class GPULight : public GPUConnectable {
protected:
    vec3 Ia;
    vec3 Id;
    vec3 Is;
    bool enabled; // indica si la llum està activa o no

public:
    /*
     * Constructor de la classe Light.
     * param Ia: component ambient de la llum.
     * param Id: component difosa de la llum.
     * param Is: component especular de la llum.
     * */
    GPULight(vec3 Ia, vec3 Id, vec3 Is): Ia(Ia), Id(Id), Is(Is) { enabled = true; };
    GPULight(vec3 Ia, vec3 Id, vec3 Is, bool isEnabled): Ia(Ia), Id(Id), Is(Is), enabled(isEnabled) { };
    
    GPULight() 
        {
            enabled = true;
            this->Ia = vec3(0.01f, 0.01f, 0.01f);
            this->Id = vec3(0.8f, 0.8f, 0.8f);
            this->Is = vec3(1.0f, 1.0f, 1.0f);
        };
    vec3 getIa() { return Ia; };
    vec3 getId() { return Id; };
    vec3 getIs() { return Is; };
    void setIa(vec3 Ia) { this->Ia = Ia; };
    void setId(vec3 Id) { this->Id = Id; };
    void setIs(vec3 Is) { this->Is = Is; };

    void setEnabled(bool enabled) { this->enabled = enabled; };
    bool isEnabled() const { return enabled; };    

    //Calcula el factor d'atenuacio de la llum al punt passat per parametre
    virtual float attenuation(vec3 point) = 0;
    //Calcula el vector L amb origen el punt passat per parametre
    virtual vec3 vectorL(vec3 point) = 0;

    //Calcula la distancia del punt a la llum
    virtual float distanceToLight(vec3 point) = 0;

    // GPUConnectable interface implementation
    virtual void toGPU(GLuint p) override;
    virtual void draw() override {};

    virtual void updateToGPU(int index);

    virtual ~GPULight() {};

};


