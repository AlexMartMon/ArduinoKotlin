
void setup() {
pinMode(3,OUTPUT);
pinMode(8,OUTPUT);
pinMode(5,OUTPUT);
pinMode(6,OUTPUT);
Serial.begin(9600);
}

void loop() {
  char var;
if (Serial.available()>0){
  var = Serial.read();
}
switch (var){
  case 'A':
  digitalWrite(3,LOW);
  digitalWrite(8,LOW);
  digitalWrite(5,LOW);
  digitalWrite(6,LOW);
  break;
  case 'B':
  digitalWrite(3,HIGH);
  digitalWrite(8,LOW);
  digitalWrite(5,LOW);
  digitalWrite(6,LOW);
  break;
  case 'C':
  digitalWrite(3,LOW);
  digitalWrite(8,HIGH);
  digitalWrite(5,LOW);
  digitalWrite(6,LOW);
  break;
  case 'D':
  digitalWrite(3,LOW);
  digitalWrite(8,LOW);
  digitalWrite(5,HIGH);
  digitalWrite(6,LOW);
  break;
  case 'E':
  digitalWrite(3,LOW);
  digitalWrite(8,LOW);
  digitalWrite(5,LOW);
  digitalWrite(6,HIGH);
  break;
  case 'F':
  digitalWrite(3,HIGH);
  digitalWrite(8,LOW);
  digitalWrite(5,HIGH);
  digitalWrite(6,LOW);
  break;
  case 'G':
  digitalWrite(3,LOW);
  digitalWrite(8,HIGH);
  digitalWrite(5,LOW);
  digitalWrite(6,HIGH);
  break;
}
}
