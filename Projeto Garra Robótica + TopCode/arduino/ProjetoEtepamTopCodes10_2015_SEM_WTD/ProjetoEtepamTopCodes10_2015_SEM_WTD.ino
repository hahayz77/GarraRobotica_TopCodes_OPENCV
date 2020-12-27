#include <Servo.h>

Servo servoX, servo9, servo6, servo11, servo10;
int cx=0, meiox=20, dx=0, set=0, code=0, xms=0;
unsigned int pos= 1650, posxms=1650;

void setup() {
  pinMode(22, OUTPUT); digitalWrite(22,0);
  servoX.attach(3); servo9.attach(9); servo6.attach(6); servo11.attach(11); servo10.attach(10);
  servoX.writeMicroseconds(posxms);
  SUBIR(); FECHAR(); ABRIR(); FECHAR(); ABRIR(); 
  servoX.writeMicroseconds(posxms);

  Serial.begin(9600);
}
  
void loop() {
  
  if (Serial.available()){
  cx = Serial.read();
  
  dx = meiox - cx;  
  posxms = pos;
  
  if (dx >= -2 && dx <= 2 ){
codigo:
  if (Serial.available()){
  code = Serial.read();
  if (code == 31 || code == 47 || code == 55){delay(10);}
  else{code = 0; goto codigo;}
}
  else {delay(100);goto codigo;}
  Serial.end();
  SUBIR(); BAIXAR(); PEGAR();delay(800); SUBIR();
  T_CODE(code); BAIXAR(); delay(200);
  ABRIR(); SUBIR();
  RESET(); SETUP();
  if (set==1){set=0;goto fim;}
} 
  
  xms = dx / 4;
  servoX.writeMicroseconds(posxms+=xms);
  pos = posxms + xms;

fim:;
  }
}

//------------------------------------------------------------------------------------

void ABRIR(){
  servo9.writeMicroseconds(1800);
  servo6.writeMicroseconds(1200);
  delay(1000);
}
void FECHAR(){
  servo9.writeMicroseconds(1550);
  servo6.writeMicroseconds(1450);
  delay(500);
}

void SUBIR(){
  servo11.write(180);
  servo10.write(0);
  delay(500);
}

void BAIXAR(){
  servo11.write(150);
  servo10.write(30);
  delay(800);
}

void PEGAR(){
  servo9.writeMicroseconds(1480);   servo6.writeMicroseconds(1520);delay(1000);
}

//------------------------------------------------------------------------------------

void T_CODE(int cod){
  if (cod == 31){
  cod=0;digitalWrite(22,1);delay(500);digitalWrite(22,0);
  servoX.writeMicroseconds(1800);delay(500);digitalWrite(22,1);servoX.writeMicroseconds(1800);delay(500);digitalWrite(22,0);
  BAIXAR();delay(1000);digitalWrite(22,1);}
  
  if (cod == 47){
  cod=0;digitalWrite(22,1);
  servoX.writeMicroseconds(2100);delay(500);digitalWrite(22,0);servoX.writeMicroseconds(2100);delay(500);digitalWrite(22,1);
  BAIXAR();delay(1000);}
 
  if (cod == 55){
  cod=0;digitalWrite(22,1);
  servoX.writeMicroseconds(2400);delay(500);servoX.writeMicroseconds(2400);delay(500);
  BAIXAR();delay(1000);}
}

void RESET(){
  cx=0; meiox=20; dx=0; pos= 1650; posxms=1650; code=0; xms=0;
}

void SETUP(){
  set=1;
  SUBIR();delay(200);SUBIR();delay(200);
  servoX.writeMicroseconds(posxms);delay(1000);servoX.writeMicroseconds(posxms);
  FECHAR();ABRIR(); FECHAR(); ABRIR();
  servoX.writeMicroseconds(posxms);delay(1000);servoX.writeMicroseconds(posxms);
  Serial.begin(9600); digitalWrite(22,0);
  
}
