// DECLARACIÓ DE VARIABLES
// constants won't change. They're used here to set pin numbers:
byte estatPrevi = 0;
byte currentState = 1;
float voltatgeLlindar = 5.5;
unsigned long goTime;
unsigned long timeMax = 30000;
unsigned long timeLastChange = 0;
unsigned long tempsPrimerJugador = 0;
unsigned long tempsSegonJugador = 0;
unsigned long timeFirst = 5000;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  
  // put your main code here, to run repeatedly:
  goTime = millis();
  
  // si el temps màxim s'ha superat
  if (goTime > timeMax) {

    int sensorValue = analogRead(A0);
    float voltatge = sensorValue * (5.0 / 1023.0);
    
    // si el voltatge és més petit que el voltatge llindar
    if(voltatge < voltatgeLlindar) {
      currentState = 0;
    } else {
      currentState = 1;
    }
    
    // si ha canviat l'estat
    if(currentState != estatPrevi) {
      if (currentState == 0) { 
        tempsSegonJugador += goTime - timeLastChange - timeFirst;
      } else {
        tempsPrimerJugador += goTime - timeLastChange - timeFirst;
      }
      timeLastChange = goTime;
      Serial.println(voltatge);
    }

  }

  delay(1000);
}

