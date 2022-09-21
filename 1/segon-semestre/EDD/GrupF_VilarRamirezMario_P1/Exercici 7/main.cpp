/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: mariovilar
 *
 * Created on March 4, 2021, 11:26 PM
 */

#include <stdexcept>
#include "Quadrilateral.h"
#include "QuadrilateralContainer.h"
#include "Rectangle.h"
#include "Square.h"

#include <iostream>
#include <fstream>
using namespace std;

/*
 *
 */

void retornarArees(QuadrilateralContainer* qdrlctr) { cout << qdrlctr -> getAreas() << endl; }

void afegirFigura(int &countQuadrat, int&countRectangle, QuadrilateralContainer* qdrlctr)
{
    int base, altura; char option; Quadrilateral *figura;
    // Suposarem que l'entrada de R i S és correcta sempre, en m
    cout << "(S)quare/(R)ectangle?" << endl;
    cin >> option;
    if(option == 'S' or option == 's')
    {
        countQuadrat++;
        cout << "Quadrat " << countQuadrat << endl;
        cout << "Base?: ";
        cin >> base;
        figura = new Square(base);
    }
    else
    {
        countRectangle++;
        cout << "Rectangle " << countRectangle << endl;
        cout << "Base: ";
        cin >> base;
        cout << "Altura: ";
        cin >> altura;
        figura =  new Rectangle(base,altura);
    }
    qdrlctr -> addQuadrilateral(figura);
    figura -> print();
    delete figura;
}

void llegirFitxer(int &countQuadrat, int&countRectangle, QuadrilateralContainer* qdrlctr)
{
    int base, altura; char option; Quadrilateral *figura;
    // Suposarem que l'entrada de R i S és correcta sempre
    ifstream elText;
    elText.open("entrada.txt");
    if(elText.is_open())
    {
        while(!elText.eof())
        {
            // Ara veurem que creem quadrat i rectangle per, acte seguit, destr-
            // uir-los. Ho fem perquè es vegi el flow de creació i destrucció de
            // objectes.
            
            elText >> option;
            if(option == 'S' or option == 's')
            {
                countQuadrat++;
                elText >> base;
                
                cout << "Quadrat(" << base << ")" << endl;
                figura = new Square(base);   
            }
            else
            {
                countRectangle++;
                elText >> base >> altura;
                
                cout << "Rectangle(" << base << ", " << altura << ")" << endl;
                figura = new Rectangle(base,altura);
            }
            qdrlctr -> addQuadrilateral(figura);
            delete figura;
        }
    }
    elText.close();
}

void glossariDeFigures(int &countQuadrat, int &countRectangle)
{
    cout << "Tens " << countQuadrat << " quadrats i " << countRectangle << " rectangles" << endl;
}

int main(int argc, char** argv)
{
    string array_options[] = {"Sortir", "Introduir figura", "Llegir fitxer", "Glossari de figures", "Imprimir àrea"};
    // Notem que qdrlctr no passarà pel constructor de Quadrilateral()
    QuadrilateralContainer* qdrlctr = new QuadrilateralContainer(); 
    int input, countQuadrat = 0, countRectangle = 0;
    try
    {
        do
        {
            cout << "Hola! Què vols fer?" << endl;
            for(int i = 0; i < 5; i++)
                cout << i+1 << ". " << array_options[i] << endl;
            cin >> input;
            switch(input)
            {
                case 1: cout << "Fins la propera!" << endl; break;
                case 2: afegirFigura(countQuadrat, countRectangle, qdrlctr); break;
                case 3: llegirFitxer(countQuadrat, countRectangle, qdrlctr); break;
                case 4: glossariDeFigures(countQuadrat, countRectangle); break;
                case 5: retornarArees(qdrlctr); break;
            }
        }
        while(input != 1);
    }
    catch(const invalid_argument &ex)
    {
        cout << ex.what() << endl;
    }
    
    delete qdrlctr;
    return 0;
}

/*
 * PREGUNTES:
 * 1. Què ens permet fer l’herència que no podríem fer altrament?
 * R: l'herència ens permet
 * 
 * 2. Què passa si getArea() de la classe Quadrilateral no és virtual? Per què?
 * R: si getArea() no fos virtual, aleshores el polimorfisme desapareixeria, és
 *    a dir, getArea() es convertiria en un únic mètode que no permetria distin-
 *    cions entre figures quadrilàters (quadrat, rectangle...).
 * 
 * 3. Per què els constructors i destructors els hem de cridar a les classes de-
 *    rivades i no a la classe base?
 * R: Pel paradigma de la Programació Orientada a Objectes, hem d'analitzar el
 *    programa des d'una òptica ascendent: de baix a dalt. Al seu torn, la rela-
 *    ció entre classes filla i mare segueix, es podria dir, una mena d'esquema 
 *    LIFO. Així, es crea (1) la classe mare (2) la classe filla, es destrueix 
 *    (1) la classe filla. A més, al ser virtual, el destructor, queda definit a
 *    les classes filles.
 * 
 * 4. Es pot fer que getArea() i getPerimetre() fos només un mètode de la clas-
 *    se “Quadrilateral”?
 * R: Els dos mètodes retornen un int. Donat que àrea i perímetre no tenen cap 
 *    relació entre si excepte la geomètrica, no seria convenient. En qualsevol
 *    cas, segurament es podria jugar una mica amb vectors o arrays per tal de
 *    poder passar les dues dades pel return.
 * 
 * 5. Anomena els membres de dades definits en els teus programes i digues a qu-
 *    ina classe pertanyen. Explica les decisions de visibilitat de cadascun.
 * R: Suposant que a l'enunciat s'estigui demanant els atributs, nosaltres sola-
 *    ment n'hem declarat dos al llarg de la pràctica, pertanyents a la classe
 *    Quadrilater(): base i altura. Hem suposat com a base el costat del rectan-
 *    gle, per simplicitat. Els hem visibilitzat com a protected perquè volíem
 *    que les classes filles fossin capaces d'accedir-hi. Pel que fa als mètodes
 *    tots públics, per la mateixa raó.
 * 
 * 6. L’iterador que recorre les figures, quant s’incrementa cada cop? Per què?
 * R: Pel simple fet de plantejar la pregunta, entenc que no serà un increment
 *    habitual, a priori, pel codi penjat al campus, sembla que no s'incrementi,
 *    sinó que el punter it referencia a un quadrilàter posterior en el vector. 
 *    De totes maneres, jo no ho he implementat amb iteradors perquè
 *    em donava un error: he decidit crear un atribut "area" que s'anés increme-
 *    ntant a cada nou quadrilàter afegit al vector de QuadrilateralContainer,
 *    mitjançant la funció getArea() de les pròpies figures addicionades.
 * 
 *    M'agradaria demanar, aleshores, la resolució de l'exercici 7 perquè pugui 
 *    veure com s'implementen correctament els iteradors.
 */