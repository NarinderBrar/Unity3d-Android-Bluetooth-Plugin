#include <Keypad.h>
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11);

char data[10];

void setup()
{
  Serial.begin(9600);
  BTSerial.begin(9600);

  data[0] = '<';
  data[1] = '0';
  data[2] = ',';
  data[3] = '0';
  data[4] = ',';
  data[5] = '1';
  data[6] = '0';
  data[7] = '2';
  data[8] = '4';
  data[9] = '>';
}

void loop()
{
 if (BTSerial.available())
 {
    incomingByte = BTSerial.read();

    if(incomingByte=='0')
    {
       BTSerial.println(data);
       Serial.println(data);
    }
    if(incomingByte=='1')
    {
       if(digitalRead(inputPin)==HIGH)
       {
          BTSerial.println("1");
          Serial.println("1");
       }
    }
 }
}
