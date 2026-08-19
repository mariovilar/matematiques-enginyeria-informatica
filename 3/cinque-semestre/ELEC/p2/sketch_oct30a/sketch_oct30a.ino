// DECLARACIÓ DE VARIABLES
// constants won't change. They're used here to set pin numbers:
const int buttonPin = 2;     // the number of the pushbutton pin
const int ledPin =  13;      // the number of the LED pin
////////////////////////
int voltatgeInicial = 5;
int comptadorPunts = 0;
int passLine = 0;
int passLineMin = 5;
int previousPinValue = 0;
unsigned long tempsMaxim = millis() + 60000; // un minut de duració del joc
unsigned long tempsActual = 0;
int mesuraTensio = 10;
////////////////////////
unsigned long startTime = 0;
unsigned long endTime = 0;
unsigned long duration = 0;
byte timerRunning = 0;
int maxApretant = 7000;
////////////////////////////////////////////////////////////////////////////////////////////////////////

// COS DEL PROGRAMA
void setup() {
  // put your setup code here, to run once:
  // inicialitzem que el pin del led, tot i que no ens servirà de molt
  pinMode(ledPin,OUTPUT);
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin,INPUT);
  // in the setup(), we initialize the serial monitor, too:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  temps = millis();
  // read the state of the pushbutton value:
  int buttonState = digitalRead(buttonPin);
  // we read the analog value coming from photoresistor
  int pinValue = analogRead(A0);

  if(previousPinValue < voltatgeInicial && pinValue > voltatgeInicial) {
    // si el valor anterior està per sota i l'actual està per sobre
    passLine++;
  } else if (previousPinValue > voltatgeInicial && pinValue < voltatgeInicial) {
    // o bé si el valor anterior estava per sobre de la línia i l'actual per sota
    passLine++;
  }

  // si el segon jugador és un trampós i està apretant tota l'estona
  if (timerRunning == 0 && digitalRead(button) == LOW) { 
    // el botó està apretat i el timer no estava corrent
    startTime = millis();
    timerRunning = 1;
  }
  if (timerRunning == 1 && digitalRead(button) == HIGH) {
    endTime = millis();
    timerRunning = 0;
    duration = endTime - startTime;
    if(duration >= maxApretant) {
      Serial.println("Jugador 2 ets un trampós"); break;
    }
  }
  
  // si el segon jugador ha apretat el botó 
  if (buttonState == HIGH && pinValue < voltatgeInicial) {
    if(comptadorPunts < 3) {
      comptadorPunts++;
    } else {
      Serial.println("Ha guanyat el jugador 2"); break; 
    }
  }

  // si el temps màxim que hem fixat sobrepassa l'actual, poden passar dues coses
  if(tempsMaxim - tempsActual < 0) {
    if(passoLinia >= passLineMin) {
      // si el jugador 1 ha fet el mínim de canvis de línia, guanya
      Serial.println("Ha guanyat el jugador 1"); break;
    } else {
      // en cas contrari, no guanya ningú, ja que el jugador 2 tampoc ha assolit els punts que tocaven
      Serial.println("No ha guanyat ningú"); break;
    }
    
  }
  // assignem el valor del pin actual al valor del pin anterior per a la següent iteració
  previousPinValue = pinValue;
  // prenem un delay establert, que podem modificar a les variables del programa
  delay(mesuraTensio);
}