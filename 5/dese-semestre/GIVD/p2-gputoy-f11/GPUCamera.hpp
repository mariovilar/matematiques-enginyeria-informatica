
#pragma once

#include <string>
#include <GL/glew.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "Utilities/GPUConnectable.hpp"
#include "glm/gtc/matrix_transform.hpp"

using namespace glm;
using namespace std;

#define MAXDPOST 100000
#define MINDANT -100000
#define EPS 0.001



typedef enum {PARALLELA = 0, PERSPECTIVA = 1} TipProj; 

struct Capsa2D {
    vec2 pmin;
    float a, h;
};
struct Capsa3D {
    vec3 pmin;
    float a, h, p;
};

class GPUCamera : public GPUConnectable {
public:
    explicit GPUCamera(int width, int height);

    GPUCamera(vec3 lookfrom, vec3 lookat, vec3 vup, float vfov, float dant, float dpost, int viewX, int viewY) ;
    
    virtual ~GPUCamera() {}

    void init(int a, int h, Capsa3D c);
    void setCamera(vec3 lookfrom, vec3 lookat, vec3 vup, float vfov, float dant, float dpost, float aspectRatio);

    void updateCamera(vec3 lookfrom, vec3 lookat, vec3 vup, float vfov, float dant, float dpost);
    void setObservador(vec3 lookfrom, vec3 lookat, vec3 vup);

    
    void actualitzaCamera(Capsa3D capsaMinima);
    void rotaCamera();
    void setFrustum(float vfov, float dant, float dpost, TipProj tp);

    void    toGPU(GLuint program) override;
    virtual void draw() override {};

    void CalculaModelView();
    void CalculaProjection();
    void CalculaWindow(Capsa3D);
    vec3 CalculObs(vec3 vrp, double d, double angx, double angy);

    void AjustaAspectRatioWd();
    void AmpliaWindow(double r);

    void anglesToAxis(vec3 angles);

    Capsa2D  CapsaMinCont2DXYVert( vec4 *v, int nv);

    void setViewport(int x, int y, int a, int h);
    void modelViewToGPU(GLuint program, mat4 m);
    void projectionToGPU(GLuint program, mat4 p);
    void lookFromToGPU(GLuint program, vec3 lookfrom);

    vec3   origin; // lookFrom
    vec3   vrp;    // lookAt
    vec3   vUp;
    float  distancia; // Distancia de l'origin a vrp
    double angX;      // Angles de rotacio de la camera
    double angY;
    double angZ;
    vec3   u, v, w;   // sistema de referencia de la camera

    TipProj typeProjection;
    // Plans de clipping anterior i posterior
    float zNear, zFar;

    // window
    Capsa2D window;
    float vfovRadians;

    // Viewport
    Capsa2D vp;

private:
    void VertexCapsa3D(Capsa3D capsaMinima, vec4 vaux[8]);

    mat4  viewMatrix; // Matriu model-view de la CPU
    mat4  projectionMatrix;  // Matriu projection de la CPU
};









