#include <Servo.h>

Servo servoX, servo9, servo6, servo11, servo10;
int cx=0, meiox=38, dx=0, set=0, code=0, xms=0, coin=0;
unsigned int pos= 1650, posxms=1650; 

void setup() {
  pinMode(8,OUTPUT);pinMode(7,OUTPUT);pinMode(4,OUTPUT);
  LEDS();delay(500);LEDS();
  servoX.attach(3); servo9.attach(9); servo6.attach(6); servo11.attach(11); servo10.attach(10);
  servoX.writeMicroseconds(posxms);
  SUBIR(); FECHAR(); ABRIR(); FECHAR(); ABRIR(); 
  servoX.writeMicroseconds(posxms);
  LEDS();

  Serial.begin(9600);
}
  
  
  
void loop() {

  if (coin == 1){delay(250);VERIFICAR();}
  
  else {VARRER();}
}

//------------------------------------------------------------------------------------
void VERIFICAR(){
  if (Serial.available()){
  cx = Serial.read();
  
  dx = meiox - cx;  
  posxms = pos;
  
  if (dx >= -4 && dx <= 4 ){
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
  
  xms = (dx / 25) - 2;
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
  servo11.write(170);servo10.write(10);
  delay(300);
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
  digitalWrite(8,1);
  cod=0;digitalWrite(22,1);delay(500);
  servoX.writeMicroseconds(1800);delay(500);digitalWrite(22,1);servoX.writeMicroseconds(1800);delay(500);digitalWrite(22,0);
  BAIXAR();delay(1000);digitalWrite(22,1);}
  
  if (cod == 47){
  digitalWrite(7,1);
  cod=0;delay(500);
  servoX.writeMicroseconds(2100);delay(500);servoX.writeMicroseconds(2100);delay(500);digitalWrite(22,1);
  BAIXAR();delay(1000);}
 
  if (cod == 55){
  digitalWrite(4,1);
  cod=0;delay(500);
  servoX.writeMicroseconds(2400);delay(500);servoX.writeMicroseconds(2400);delay(500);
  BAIXAR();delay(1000);}
}

void RESET(){
  cx=0; meiox=38; dx=0; pos= 1650; posxms=1650; code=0; xms=0; coin=0;
}

void SETUP(){
  set=1; pos=1650; posxms=1650;
  SUBIR(); delay(200); SUBIR();delay(200);
  servoX.writeMicroseconds(posxms);delay(1000);servoX.writeMicroseconds(posxms);
  FECHAR(); ABRIR(); FECHAR(); ABRIR();
  servoX.writeMicroseconds(posxms);delay(1000);servoX.writeMicroseconds(posxms);
  Serial.begin(9600); RESET(); LEDS();
  
}
void LEDS(){
  digitalWrite(8,1);digitalWrite(7,1);digitalWrite(4,1);delay(500);
  digitalWrite(8,0);digitalWrite(7,0);digitalWrite(4,0);
}

void VARRER(){
  delay(5000);  
  
  if (Serial.available()){coin = 1;LEDS();delay(1000);goto principal;}
  else {pos = pos - 200; servoX.writeMicroseconds(pos);  delay(5000);}
  
  if (Serial.available()){coin = 1;LEDS();delay(1000);goto principal;}
  else {pos = pos - 200; servoX.writeMicroseconds(pos);  delay(5000);}
  
  if (Serial.available()){coin = 1;LEDS();delay(1000);goto principal;}
  else {pos = pos + 200; servoX.writeMicroseconds(pos);  delay(5000);}
  
  if (Serial.available()){coin = 1;LEDS();delay(1000);goto principal;}  
  else {pos = pos + 200; servoX.writeMicroseconds(pos);}  
principal:; 
}
