# PR1
Codi base de la PR1 de l'assignatura de Software Distribuït de la UB

## Introducció
Aquest projecte té com a objectiu facilitar la creació d'una aplicació distribuïda en Java que consta d'un client, un servidor i utilitats compartides. La idea principal és permetre que diferents usuaris interactuïn mitjançant diferents operacions per a crear una simulació de l'enfonsar la flota.

## Arquitectura
El codi està estructurat en tres parts:
- **client**: Es tracta d'una aplicació que realitza peticions al servidor. Està dissenyat per permetre que diferents usuaris interactuïn amb el sistema mitjançant operacions com CREATE, JOIN, i moltes més. El client inclou la lògica necessària per interpretar les respostes que proveeix el servidor.
- **server**: El servidor és el cor del projecte. Escolta les peticions del client i respon d'acord amb les ordres enviades. Inclou la gestió de la connexió i la lògica de negoci per processar les demandes rebudes, mantenint la coherència de l'estat del joc.
- **comUtils**: Aquesta carpeta conté el codi compartit entre el client i el servidor. Les utilitats definides aquí inclouen funcions comuns que faciliten el maneig de dades i les operacions que es duen a terme en ambdues parts del sistema.

Per entendre millor com interactuen aquests components, es podria consultar el diagrama d'arquitectura adjunt o una descripció detallada en aquesta secció.

## Com compilar, encapsular i executar el codi
Per executar el codi cal tenir instal·lat el JDK de Java. Si hi ha dubtes a l'hora d'executar, les comandes generals es poden veure en més detall usant la comanda `HELP`. 
```java
System.out.println("CREATE <playerName> <w> <h> <v1> <v2> <v3> <v4> <v5> <ai (0 or 1)>");
System.out.println("JOIN <playerName>");
System.out.println("GETCONFIG <playerId> <gameId>");
System.out.println("ADDVESSEL <playerId> <gameId> <type> <ri> <ci> <rf> <cf>");
System.out.println("SHOT <playerId> <gameId> <r> <c>");
System.out.println("GETSTATUS <playerId> <gameId>");
System.out.println("LEAVE <playerId> <gameId>");
```
Abans de tot, assegura't d'haver instal·lat el JDK de Java. El procediment general per a compilar i empaquetar el codi és el següent:
1. **Compilació i empaquetat:**  
   Utilitza Maven per compilar i empaquetar tant el client com el servidor. Això generarà els fitxers JAR que contenen totes les dependències necessàries.  
   Exemple:
   ```bash
   mvn clean package
   ```
2. **Execució del servidor:**  
   Un cop empaquetat, executa el JAR del servidor. Hi ha una opció addicional (compte perquè estan a diferents branques del control de versions), en funció de si volem testejar la funcionalitat multiplayer o no (recordar els comentaris que hem fet a la memòria sobre aquesta). Escull la comanda que correspongui a la configuració que vulguis utilitzar:
   ```bash
   java -jar Server/target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar -p 8080
   ```
   o alternativament:
   ```bash
   java -jar Server/target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar -p 8080 --multiplayer
   ```
   Aquesta comanda inicia el servidor a la port indicada.

3. **Execució del client:**  
   De manera similar, executa el JAR del client. Alguns exemples de configuració dels paràmetres (adreça de l'amfitrió i port) són els següents:
   ```bash
   java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h sd.xbaro.eu -p 22026
   ```
   o bé:
   ```bash
   java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h localhost -p 4205
   ```

Un cop instal·lat, es pot executar el codi de la següent manera:
### Servidor
```bash
mvn clean package
java -jar Server/target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar -p 22026
java -jar Server/target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar -p 4205
```

### Client
```bash
mvn clean package
java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h sd.xbaro.eu -p 22026
java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h localhost -p 4205
```
En cas d'estar testejant la funcionalitat multiplayer, hauríem de crear un altre terminal i executar un segon Client, que el Servidor reconeixerà i gestionarà:
```bash
java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h sd.xbaro.eu -p 22026
java -jar Client/target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar -h localhost -p 4205
```

## Consells addicionals
- **Verificació de dependències:**  
  Assegura't que Maven ha descarregat totes les dependències del projecte correctament. En cas de problemes, pots provar a executar:
  ```bash
  mvn clean install
  ```
- **Diferents entorns:**  
  Recorda que algunes configuracions (com les adreces i ports) poden variar en funció de la xarxa o el servidor on s'implanti.

- **Debug i seguiment d'errors:**  
  Si trobes errors a l'executar el client o el servidor, revisa les sortides per consola per identificar missatges d'error específics. Això facilitarà la localització del problema.

## Contribució
Si vols contribuir a aquest projecte, segueix els següents passos:
1. Fes un fork del repositori.
2. Crea una nova branca per a la teva funcionalitat o correcció.
3. Realitza els canvis i envia un pull request.
  
Segueix les normes de codificació establertes al projecte per garantir un codi net i mantenible.