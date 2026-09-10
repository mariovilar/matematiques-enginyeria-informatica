---
author:
- Xavi Baró, Blai Ras, Eloi Puertas
date: Febrer 2024
title: Pràctica 0 - ComUtils Client Servidor (Software distribüit)
---

ComUtils
========

L'objectiu docent d'aquesta sessió és entendre els mecanismes d'entrada
i sortida. Concretament heu d'entendre i posteriorment utilitzar la
classe ComUtils que trobareu en fer el fork del repo de la pràctica 0.

Tasques a realitzar
===================

-   Creació de compte Github si no es te.

-   Creació de grups per parelles.

-   Acceptar link d'invitació a GithubClasses i posar el grup al que heu
    estat assignat.

-   Baixeu el codi de la pràctica 0 i exteneu el projecte segons indica
    el següent apartat.

Classe ComUtils
===============

La classe ComUtils ens ajudarà a l'hora d'enviar missatges a través d'un
socket. Fixeu-vos que el constructor de la classe rep un objecte de la
classe Socket, del qual es treuen dos DataStream, un d'entrada i un de
sortda. Fixeu-vos en els següents mètodes:

-   `String: read_string()`: Llegeix un string de mida fixa. La mida ve
    fixada per la variable global estàtica STRSIZE, que ara val 40, per
    tant sempre llegirà 40 bytes.

-   `int: read_int32()`: Llegeix un sencer de 32 bits, per tant ocupa 4
    bytes. El número es llegeix seguint la convenció del BigEndian.

-   `String: read_string_variable(int head_size)`: Llegeix un string de
    mida variable. La trama ha de tenir una capçalera en format texte
    que indiqui quants caràcters s'han de llegir. `Head_size` diu de
    quants caràcters està formada la capçalera. La mida de la capçalera
    ha de ser suficient per indicar quants dígits es necessiten per
    expressar la mida del missatge.

    Exemple: `read_string_variable(2)`, espera un missatge amb una
    capçalera de dos caràcters en format ascii que indiqui el nombre de
    caràcters a llegir

-   `write_string(String s)`: Escriu un string de mida fixa. La mida ve
    fixada per la variable global estàtica STRSIZE, que ara val 40, si
    l'string medeix menys s'omple amb espais al final.

-   `write_int32(int)`: Escriu un sencer de 32 bits, per tant ocupa 4
    bytes. El número s'escriu seguint la convenció del BigEndian.

-   `write_string_variable(int head_size, String s)`: Escriu un string de
    mida variable. La trama ha de tenir una capçalera que indiqui quants
    caràcters s'han de llegir. La trama ha de tenir una capçalera en
    format text que indiqui quants caràcters s'han de llegir. `Head_size`
    diu de quants caràcters està formada la capçalera. La mida de la
    capçalera ha de ser suficient per indicar quants dígits es
    necessiten per expressar la mida del missatge. Els caràcters que no
    s'usin de la capçalera s'ompliran amb el caràcter '0'

    Exemple: `write_string_variable(2,\"Hola!\")`, escriurà una trama amb
    una capçalera de dos caràcters que indicarà el nombre de caràcters a
    llegir (5), com que no necessitem tots els caràcters de la capçalera
    ho omplirem amb un '0'. Trama: 05Hola!

Llegiu el codi dels demés mètodes auxiliars per entendre com es passa de
bytes a qualsevol tipus natiu del java. Assegureu-vos d'entendre-ho bé,
no seguiu endevant fins que no hàgiu entés tot el codi a la perfecció.

Tasques
=======

-   La primera tasca a realitzar és iniciar el projecte d'aquesta
    pràctica. Per aconseguir això fem:

        mvn clean package    

-   La segona tasca és passar el test que hi ha d'exemple en el projecte.

        mvn test

-   La tercera és fer codi en el mètode `comUtilsService.writeTest()` i
    `comUtilsService.readTest()` per tal de poder escriure ints, strings
    o el que volgueu provar.

-   La quarta és la de fer un mètode main per testejar els mètodes
    anteriors, fent servir l'anterior constructor, de la següent forma:

            public static void main(String[] args) {
                  File file = new File("test");
                  try {
                      file.createNewFile();
                      ComUtilsService comUtilsService = new ComUtilsService(new FileInputStream(file), new FileOutputStream(file));
                      comUtilsService.writeTest();
                      System.out.println(comUtilsService.readTest());
                    }
                    catch(IOException e)
                    {
                        System.out.println("Error Found during Operation:" + e.getMessage());
                        e.printStackTrace();
                    }
- Un cop tingueu el codi escrit heu de generar el fitxer .jar amb la següent comanda:

        mvn clean package
        
També podeu fer servir l'extensió maven de Visual Studio Code per fer-ho. En aquest cas aneu a maven i dins del Lifecycle escolliu package.


Execució
========

-   La última cosa per fer és executar el codi:

        java -jar ./target/ComLib-1.0-SNAPSHOT.jar 

-   Executeu el programa i responeu les següents preguntes en el Pull Request:

    1.  Si canvieu el format o l'ordre en el writeTest i no en el
        readTest, quin error dona?

    2.  Si obriu el fitxer test amb un editor com el vi hi veieu
        exactament el mateix que heu escrit? Quins caràcters són els que
        no veieu?

    3.  El nombre de bytes que ocupa el fitxer és exactament igual al
        nombre de bytes que hi heu escrit?

    4.  Sabríeu detectar les trames que heu escrit mirant el fitxer
        test?

-   Ara feu un mètode writeChar i readChar que escrigui i llegeixi un
    sol caràcter.

-   Proveu que funcioni correctament.

-   Feu més casos de test en el fitxer corresponent a pract0/src/test i   executeu amb 

        mvn test
