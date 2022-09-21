/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   exercici5.cpp
 * Author: mariovilar
 *
 * Created on February 28, 2021, 11:45 AM
 */

#include <iostream>
#include <stdexcept>
#include "Rectangle.h"
#include "Square.h"
#include<fstream>
using namespace std;

/*
 *
 */

void afegirFigura(int &countQuadrat, int&countRectangle)
{
    int base, altura; char option;
    // Suposarem que l'entrada de R i S és correcta sempre, en m
    cout << "(S)quare/(R)ectangle?" << endl;
    cin >> option;
    if(option == 'S' or option == 's')
    {
        countQuadrat++;
        cout << "Quadrat " << countQuadrat << endl;
        cout << "Base?: ";
        cin >> base;
        
        Square quadrat(base);
        quadrat.print();
    }
    else
    {
        countRectangle++;
        cout << "Rectangle " << countRectangle << endl;
        cout << "Base: ";
        cin >> base;
        cout << "Altura: ";
        cin >> altura;
        
        Rectangle rect(base, altura);
        rect.print();
    }
}

void llegirFitxer(int &countQuadrat, int&countRectangle)
{
    int base, altura; char option;
    // Suposarem que l'entrada de R i S és correcta sempre
    ifstream elText;
    elText.open("entrada.txt");
    if(elText.is_open())
    {
        while(!elText.eof())
        {
            elText >> option;
            if(option == 'S' or option == 's')
            {
                countQuadrat++;
                elText >> base;
                
                cout << "Quadrat(" << base << ")" << endl;
                Square quadrat(base);
                
            }
            else
            {
                countRectangle++;
                elText >> base;
                elText >> altura;
                
                cout << "Rectangle(" << base << ", " << altura << ")" << endl;
                Rectangle rect(base, altura);
            }
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
    string array_options[] = {"Sortir", "Introduir figura", "Llegir fitxer", "Glossari de figures"};
    int input, countQuadrat = 0, countRectangle = 0;
    try
    {
        do
        {
            cout << "Hola! Què vols fer?" << endl;
            for(int i = 0; i < 4; i++)
                cout << i+1 << ". " << array_options[i] << endl;
            cin >> input;
            switch(input)
            {
                case 1: cout << "Fins la propera!" << endl; break;
                case 2: afegirFigura(countQuadrat, countRectangle); break;
                case 3: llegirFitxer(countQuadrat, countRectangle); break;
                case 4: glossariDeFigures(countQuadrat, countRectangle); break;
            }
        }
        // Suposarem que el programa també finalitza si es demana el glossari de figures
        while(input != 1);
    }
    catch(const invalid_argument &ex)
    {
        cout << ex.what() << endl;
    }           
    return 0;
}
