# Sessió de proves creuades
| Grup         |   Components       |      Usuari GitHub     |
|--------------|--------------------|------------------------|
|    F09       |  Vilar, Mario      |      mariovilar        |
|              |  Díez, David       |      david-diiez       |


## Sessió de proves creuades

### La vostra pràctica
En aquest apartat cal explicar l'estat inicial de la vostra pràctica:

- __Servidor__
- [X] El meu __Servidor__ arranca i permet que es connectin __Clients__, assignant-los a una partida.
- [X] El meu __Servidor__ té implementada la fase de configuració en que els __Clients__ afegeixen els vaixells al tauler.
- [X] El meu __Servidor__ implementa la dinàmica de joc, en la qual els __Clients__ van disparant en posicions per enfonsar els vaixells.
- [] El meu __Servidor__ implementa el joc **multi-jugador**.
- __Client__
- [X] El meu __Client__ es connecta correctament al servidor, afegint-se a una partida.
- [X] El meu __Client__ té implementada la fase de configuració en que l'usuari pot anar afegint els vaixells al tauler.
- [X] El meu __Client__ implementa la dinàmica de joc, en la qual l'usuari va indicant les posicions on disparar per enfonsar els vaixells de l'oponent.

També haureu de reportar els errors que us hagin reportat, indicant l'error, si l'heu pogut solucionar i quin grup us l'ha reportat.

(Ho deixò tot redactat a la última pregunta)

A banda de les proves realitzades durant la sessió de proves creuades, en aquest apartat caldrà incloure les proves que heu realitzat en el vostre pròpi codi, tant d'usuari com amb proves unitàries JUnit.

    Les proves que hem realitzat sobre el nostre codi són
    1. Connectar-se i desconnectar-se des del client -> el servidor segueix funcionant correctament, es creen partides diferents i tot va bé.
    2. Connectar-se i desconnectar-se des del servidor -> a la següent instrucció, el client rep un broken pipe i la partida s'acaba.
    3. Crear una partida.
    4. Fer create seguit de join funciona correctament.
    5. Fer create seguit de getconfig dona un error, ja que getconfig s'executa quan l'estat és SETUP i no WAITING_PLAYERS.
    6. Fer leave d'una partida inexistent retorna el missatge "Partida no disponible".
    7. Fer leave d'una partida existent notifica als jugadors i tanca la partida.
    8. En tots els mètodes es comprova que la partida existeixi.
    9. Getstatus funciona correctament.
    10. Si es vol fer ADDVESSEL abans que tots els jugadors estiguin llestos, retorna un error d'estat.
    11. Afegir un vaixell funciona correctament.
    12. Afegir un vaixell en una posició ja ocupada retorna "coordenada incorrecta".
    13. Intentar crear dues partides seguides retorna que ja hi ha una partida creada.
    14. Si es crea un bot, tot continua funcionant correctament (es col·loquen els vaixells on toca, ben èpic).
    15. Si es col·loca un vaixell amb una mida incorrecta, es retorna que les coordenades són incorrectes.
    16. Si un jugador ja ha col·locat tots els vaixells d’un tipus, no se’n poden afegir més.
    17. Es gestionen correctament els torns per disparar (comença el jugador que ha creat la partida).

    A més a més hem realitzat moltes proves unitàries JUnit per provar el nostre codi. Aquestes són
    1. Prova de CreateMessage:
        Es crea un missatge CreateMessage amb certs paràmetres.
        S'escriu el missatge utilitzant comUtils.writeMessage(message).
        Es llegeix el missatge utilitzant comUtils.readMessage().
        Es verifica que el missatge llegit és igual al missatge escrit utilitzant assertEquals(message, readMessage).

    2. Prova de OkMessage:
        Es crea un fitxer temporal per simular l'entrada i sortida de dades.
        Es crea un missatge OkMessage amb certs paràmetres.
        S'escriu el missatge utilitzant comUtils.writeMessage(message).
        Es llegeix el missatge utilitzant comUtils.readMessage().
        Es verifica que el missatge llegit és igual al missatge escrit utilitzant assertEquals(message, readMessage).

    3. Prova de testAddPlayerSuccess:
        Aquesta prova verifica que un jugador es pot afegir correctament al joc. Es comprova que el nombre de jugadors augmenta en afegir un nou jugador i que el jugador afegit té un identificador vàlid.

    4. Prova de testGetNumPlayersAndGetPlayerId:
        Aquesta prova comprova que es poden obtenir correctament el nombre de jugadors i els identificadors dels jugadors afegits. Es verifica que el nombre de jugadors és correcte després d'afegir jugadors i que els identificadors retornats són els esperats.

    5. Prova de testAddVesselAndRemainingVessels:
        Aquesta prova verifica que es poden afegir vaixells correctament al joc i que el nombre de vaixells restants es redueix adequadament. Es comprova que es poden afegir vaixells de diferents tipus i que el nombre de vaixells restants es gestiona correctament.

    6. Prova de testAddVesselEdgeCases:
        Aquesta prova comprova els casos límit en afegir vaixells. Es verifica que no es poden afegir vaixells amb coordenades fora dels límits del tauler, amb mides inadequades o que solapin amb altres vaixells. També es comprova que es poden afegir vaixells correctament dins dels límits i amb mides adequades.

    7. Prova de testIsPlayerReady:
        Aquesta prova verifica que es pot comprovar correctament si un jugador està llest per començar el joc. Es comprova que un jugador està llest després d'afegir tots els vaixells necessaris i que no està llest si encara falten vaixells per afegir.

    8. Prova de testShotInvalidCoordinates:
        Aquesta prova comprova que els trets amb coordenades invàlides es gestionen correctament. Es verifica que el joc retorna un error o un resultat adequat quan es realitza un tret fora dels límits del tauler.

    9. Prova de testShotHit:
        Aquesta prova verifica que els trets que impacten en un vaixell es gestionen correctament. Es comprova que el joc detecta correctament un impacte en un vaixell i actualitza l'estat del vaixell i del tauler adequadament.

    10. Prova de testShotSink:
        Aquesta prova comprova que els trets que enfonsen un vaixell es gestionen correctament. Es verifica que el joc detecta correctament quan un vaixell ha estat completament enfonsat i actualitza l'estat del joc adequadament.

    11. Prova de testNotifyStatus:
        Aquesta prova verifica que el joc notifica correctament l'estat del joc als jugadors. Es comprova que els jugadors reben notificacions adequades sobre l'estat del joc, com ara impactes, enfonsaments i altres esdeveniments rellevants.

    12. Prova de testLeaveGameAndEndGame:
        Aquesta prova comprova que un jugador pot abandonar el joc i que el joc finalitza correctament quan tots els jugadors han abandonat. Es verifica que el joc gestiona adequadament la sortida dels jugadors i que l'estat del joc es manté coherent fins al final.

### Proves realitzades

Per cada grup que hagueu provat, caldra informar del nom del Grup que s'ha avaluat i la informació bàsica equivalent a la anterior:

#### GRUP 7
- __Client__
- [x] El nostre __Servidor__ arranca i permet que es connectin __Clients__, assignant-los a una partida.
- [x] El nostre __Servidor__ funciona correctament quan els __Clients__ afegeixen els vaixells al tauler.
- [x] El nostre __Servidor__ funciona correctament en quant a dinàmica de joc, els __Clients__ poden disparar en posicions per enfonsar els vaixells.
- [] No hem pogut provar el **multi-jugador** ja que no està implementat.

**Altres observacions:** L'únic problema que va haver-hi és amb la visualització dels hits que feia el Client sobre el bot. L'implementació del seu grup interpretava els FAIL del client com un HIT, quan no era el cas, ja que no feia una bona gestió dels casos (quan feia el Shot, tenien posat que 1 era HIT en lloc de FAIL). S'ha de dir que el protocol demana una gestió molt poc intuïtiva pel que fa els bytes dels vaixells, pel que és un error completament comprensible. Com comentarem més endavant, no fèiem una bona gestió quan el Client feia LEAVE i tancava el joc i hem hagut d'afegir un catch de tipus `IOException` per quan el Client decidia marxar del joc.

Ja per últim, ens agradaria remarcar que la visualització dels boards dels nostres companys han estat generalment millors que la nostra, pel que un dels objectius que tenim per aquesta setmana és millorar la nostra implementació en aquest sentit.

#### GRUP 8
- __Client__
- [x] El nostre __Servidor__ arranca i permet que es connectin __Clients__, assignant-los a una partida.
- [x] El nostre __Servidor__ funciona correctament quan els __Clients__ afegeixen els vaixells al tauler.
- [x] El nostre __Servidor__ funciona correctament en quant a dinàmica de joc, els __Clients__ poden disparar en posicions per enfonsar els vaixells.
- [] No hem pogut provar el **multi-jugador** ja que no està implementat.

**Altres observacions:** Quan s'afegeixen vaixells molt ràpid hem tingut algun problema de "coordenada incorrecta". L'error que retornem és molt genèric a causa que el mètode `addVessels()` ha de retornar (la interfície que se'ns ha donat és molt explícita al respecte) un booleà. Ens agradaria modificar-ho per informar l'usuari quin tipus d'error concret s'ha donat, però no volem contradir l'enunciat de la pràctica. A la vegada, ells comencen a comptar les coordenades des de (0,0) i nosaltres des de (1,1), pel que hem obtingut un error de coordenades incorrectes a l'intentar afegir un vaixell. Després de solucionar aquest incident, no hem tingut cap problema fins al LEAVE. El comentari anterior sobre el LEAVE aplica també en les proves amb aquest grup.

Ja per últim, ens agradaria remarcar que la visualització dels boards dels nostres companys han estat generalment millors que la nostra, pel que un dels objectius que tenim per aquesta setmana és millorar la nostra implementació en aquest sentit.

#### GRUP 1O
- __Servidor__
- [x] El seu __Servidor__ arranca i permet que es connectin __Clients__, assignant-los a una partida.
- [x] El seu __Servidor__ té implementada la fase de configuració en que els __Clients__ afegeixen els vaixells al tauler.
- [x] El seu __Servidor__ implementa la dinàmica de joc, en la qual els __Clients__ van disparant en posicions per enfonsar els vaixells.
- [] El seu __Servidor__ implementa el joc **multi-jugador**.
- __Client__
- [x] El nostre __Client__ es connecta correctament al servidor, afegint-se a una partida.
- [x] El nostre __Client__ té implementada la fase de configuració en que l'usuari pot anar afegint els vaixells al tauler.
- [x] El nostre __Client__ implementa la dinàmica de joc, en la qual l'usuari va indicant les posicions on disparar per enfonsar els vaixells de l'oponent.


En provar amb el grup 10 hem detectat que el nostre Client no gestionava correctament el final de partida i continuava intentant llegir del servidor. Això generava errors de connexió. Per solucionar-ho, hem afegit un bloc catch per capturar excepcions de tipus `IOException` dins el bucle principal del Client de manera que ara es tanca la connexió correctament quan la partida finalitza. El grup 10 no ha hagut de fer cap canvi ja que el problema era en la nostra implementació.


#### GRUP 11
- __Servidor__
- [x] El seu __Servidor__ arranca i permet que es connectin __Clients__, assignant-los a una partida.
- [x] El seu __Servidor__ té implementada la fase de configuració en que els __Clients__ afegeixen els vaixells al tauler.
- [x] El seu __Servidor__ implementa la dinàmica de joc, en la qual els __Clients__ van disparant en posicions per enfonsar els vaixells.
- [] El seu __Servidor__ implementa el joc **multi-jugador**.
- __Client__
- [x] El nostre __Client__ es connecta correctament al servidor, afegint-se a una partida.
- [x] El nostre __Client__ té implementada la fase de configuració en que l'usuari pot anar afegint els vaixells al tauler.
- [x] El nostre __Client__ implementa la dinàmica de joc, en la qual l'usuari va indicant les posicions on disparar per enfonsar els vaixells de l'oponent.

Amb el grup 11 les primeres proves han funcionat correctament. Tanmateix en provar algunes combinacions més inusuals hem detectat que el seu servidor enviava un missatge `null`, fet que provocava una excepció quan el nostre Client intentava llegir-lo. Per gestionar-ho hem afegit un bloc `catch` per capturar `NullPointerException` i el problema s’ha resolt. En aquest cas el grup 11 haurà de revisar el seu servidor per evitar retornar missatges `null`.

A més hem detectat un altre problema relacionat amb l'ordre `LEAVE`: el nostre Client no era capaç d’identificar que la partida havia acabat. Per descartar que el problema fos nostre hem repetit la mateixa prova amb el grup 10 i no s’ha produït cap error. En revisar el servidor del grup 11 hem comprovat que no envien cap missatge de tipus `GameStatus` quan es canvia l’estat del joc. Això provoca que el nostre Client no rebi la notificació i, per tant, no pugui tancar la partida correctament. En aquest cas no hem hagut de modificar res atès que el problema resideix en el seu servidor.

A més a més, caldrà explicar les proves fetes, els resultats obtinguts i si s'ha detectat errors o no.
