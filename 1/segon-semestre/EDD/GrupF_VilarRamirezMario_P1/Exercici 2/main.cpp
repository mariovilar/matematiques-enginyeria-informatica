/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: mariovilar
 *
 * Created on March 3, 2021, 7:25 PM
 */

#include <iostream>
#include <string>
#include <vector>
using namespace std;

/*
 * 
 */

void get_name(string &name)
{
    string nou;
    cout << "Hola, com et dius?" << endl;
    cin >> nou;
    
    name = nou;
}

void mostra(string nom, vector<string> array)
{
    int input = 0;
    do
    {
        cout << "Hola " << nom << "! Què vols fer?" << endl;
        for(int j = 0; j < array.size(); ++j)
            cout << j+1 << ". " << array[j] << endl;

        cin >> input;
        switch(input)
        {
            case 1: cout << "Fins la propera, " << nom << endl; break;
            case 2: cout << "Benvolgut a l'assignatura d'estructura de dades, " << nom << endl; break;
            case 3: get_name(nom); break;
        }
    }
    while(input != 1);
}

int main(int argc, char** argv)
{
    string name; 
    string array_options[] = {"Sortir", "Benvinguda", "Redefinir el nom"};
    size_t size = 3;
    
    vector<string> array(size);
    // Transformació a vector
    for(int i = 0; i < array.size(); ++i)
        array[i] = array_options[i]; 
    
    get_name(name);
    mostra(name, array);
    
    return 0;
}


/*
 * El meu codi canviava bé de nom la primera vegada, però a partir de la segona
 * sol·licitud em retornava (1) un espai en blanc o (2) el nom de l'anterior cop,
 * és a dir, no em deixava introduir les noves dades. El principal dubte, doncs,
 * ha sigut com resoldre això.
 * Simplement he hagut de canviar el getline(cin,name) per un cin >> name
 */

