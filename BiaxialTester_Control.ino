#include "HX711-multi.h"

// Pins to the load cell amp
#define CLK 2      // clock pin to the load cell amp
#define DOUT1 3    // data pin to the first lca
#define DOUT2 4    // data pin to the second lca
#define DOUT3 5    // data pin to the third lca
#define DOUT4 6    // data pin to the forth lca

#define TARE_TIMEOUT_SECONDS 4

byte DOUTS[4] = {DOUT1, DOUT2, DOUT3, DOUT4};

#define CHANNEL_COUNT 4

long int results[CHANNEL_COUNT];

HX711MULTI scales(CHANNEL_COUNT, DOUTS, CLK);

long int cal_values[4] ;


const int dirPin[] = {22, 28, 34, 40};
const int stepPin[] = {24, 30, 36, 42};
const int en[] = {26, 32, 38, 44};

const int steps = 200;
int microPausa = 1000;


void moveRev(int milimeters, int data) {
    if (data < 4 && data >= 0) { //Cambiar direccion de motor en concreto
    //Multiply 100 (steps/milimeter) to do the steps
    for (int x = 0; x <= milimeters * 100/4 ; x++) {
        digitalWrite(stepPin[data], HIGH);
      delayMicroseconds(microPausa);
        digitalWrite(stepPin[data], LOW);
      delayMicroseconds(microPausa);
    }
  }
  else if (data == 4) { //Cambiar direccion de motores 1-2
    //Multiply 100 (steps/milimeter) to do the steps
    for (int x = 0; x <= milimeters * 100/4 ; x++) {
      for (int i = 0; i < 2; i++) {
        digitalWrite(stepPin[i], HIGH);
      }
      delayMicroseconds(microPausa);
      for (int i = 0; i < 2; i++) {
        digitalWrite(stepPin[i], LOW);
      }
      delayMicroseconds(microPausa);
    }
  }
  else if (data == 5) { //Cambiar direccion de motores 3-4
    //Multiply 100 (steps/milimeter) to do the steps
    for (int x = 0; x <= milimeters * 100/4 ; x++) {
      for (int i = 2; i < 4; i++) {
        digitalWrite(stepPin[i], HIGH);
      }
      delayMicroseconds(microPausa);
      for (int i = 2; i < 4; i++) {
        digitalWrite(stepPin[i], LOW);
      }
      delayMicroseconds(microPausa);
    }
  }
  else if (data == 6) { //Cambiar direccion de todos los motores
    //Multiply 100 (steps/milimeter) to do the steps
    for (int x = 0; x <= milimeters * 100/4 ; x++) {
      for (int i = 0; i < 4; i++) {
        digitalWrite(stepPin[i], HIGH);
      }
      delayMicroseconds(microPausa);
      for (int i = 0; i < 4; i++) {
        digitalWrite(stepPin[i], LOW);
      }
      delayMicroseconds(microPausa);
    }
  }
  
}

void enableMotor(int data) {
  if (data < 4 && data >= 0) { //Desabilitar motor en concreto
    digitalWrite(en[data], !digitalRead(en[data]));
  }
  else if (data == 4) { //Habilitar/Desabilitar motores 1-2
    for (int i = 0; i < 2; i++) {
      digitalWrite(en[i], !digitalRead(en[i]));
    }
  }
  else if (data == 5) { //Habilitar/Desabilitar motores 3-4
    for (int i = 2; i < 4; i++) {
      digitalWrite(en[i], !digitalRead(en[i]));
    }
  }
  else if (data == 6) { //Habilitar/Desabilitar todos los motores
    for (int i = 0; i < 4; i++) {
      digitalWrite(en[i], !digitalRead(en[i]));
    }
  }
}

void dirMotor(int data) {
  if (data < 4 && data >= 0) { //Cambiar direccion de motor en concreto
    digitalWrite(dirPin[data], !digitalRead(dirPin[data]));
  }
  else if (data == 4) { //Cambiar direccion de motores 1-2
    for (int i = 0; i < 2; i++) {
      digitalWrite(dirPin[i], !digitalRead(dirPin[i]));
    }
  }
  else if (data == 5) { //Cambiar direccion de motores 3-4
    for (int i = 2; i < 4; i++) {
      digitalWrite(dirPin[i], !digitalRead(dirPin[i]));
    }
  }
  else if (data == 6) { //Cambiar direccion de todos los motores
    for (int i = 0; i < 4; i++) {
      digitalWrite(dirPin[i], !digitalRead(dirPin[i]));
    }
  }
}

//Funcion para medir en las celulas de carga
void tare() {
  bool tareSuccessful = false;

  unsigned long tareStartTime = millis();
  while (!tareSuccessful && millis()<(tareStartTime+TARE_TIMEOUT_SECONDS*1000)) {
    tareSuccessful = scales.tare(20,10000);  //reject 'tare' if still ringing
  }
}

//Mandar la información de las celulas de carga
void sendRawData() {
  scales.read(results);
  for (int i=0; i<scales.get_count(); ++i) {;
    Serial.print( (int)((results[i] - cal_values[i])/9.8));  
    Serial.print( (i!=scales.get_count()-1)?"\t":"\n");
  }  
  delay(10);
}

void setup() {
  Serial.begin(115200);
  Serial.setTimeout(1000);
  tare();
  scales.read(results);
  for (int i=0; i<scales.get_count(); ++i) {
    cal_values[i] = results[i];
  }
  for (int i = 0; i < 4; i++) {
    pinMode(dirPin[i], OUTPUT);
    pinMode(stepPin[i], OUTPUT);
    pinMode(en[i], OUTPUT);
  }

  for (int i = 0; i < 4; i++) {
    digitalWrite(en[i], LOW);
    digitalWrite(dirPin[i], HIGH);
  }
}

void loop() {

  
  if (Serial.available()) {
    tare();
    sendRawData();
    String command;
    char parameter,motor;
    int data,milimeters;
    command = Serial.readString();

    parameter = command.charAt(0);
    if (parameter < 'Z') parameter -= 32;
    switch (parameter) {
      case 'm': //Mover motores
        milimeters = 0;
        motor = command.charAt(1);
        data = atoi(&motor);
        for(int j = 2; j < command.length() -1; j++){ 
          parameter = command.charAt(j);
          if(j != 2) milimeters = milimeters * 10; 
          milimeters += atoi(&parameter);
        }
        moveRev(milimeters,data);
        break;

      case 'e': //HabilitarMotores
        parameter = command.charAt(1);
        data = atoi(&parameter);
        enableMotor(data);
        break;

      case 'd': //DireccionMotores
        parameter = command.charAt(1);
        data = atoi(&parameter);
        dirMotor(data);
        break;

      case 's': //DireccionMotores
        parameter = command.charAt(1);
        data = atoi(&parameter);
        Serial.print("s");
        Serial.print(digitalRead(dirPin[0]));
        Serial.print(" ");
        Serial.print(digitalRead(dirPin[1]));
        Serial.print(" ");
        Serial.print(digitalRead(dirPin[2]));
        Serial.print(" ");
        Serial.print(digitalRead(dirPin[3]));
        Serial.print("\n");
        break;
      default:
        break;
    }
    Serial.flush();

  }





}
