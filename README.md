# Bluetooth_Serial
Bluetooth 2-way serial communication between Android and Arduino with HC-05 module
No additional libraries required.
Software used: Android Studio and Arduino IDE

This is a base project for communicating between an Android device and an Arduino with HC-05 module installed.
The current setup turns a LED on and off, and the Arduino sends back the strings sent by the Android device.

Arduino setup:
TX0 -> HC-05 RX
RX0 -> HC-05 TX Warning: this overrides the USB serial communication with your PC. Disconnect wires to transfer code to Arduino.
5V -> HC-05 VCC
GND -> HC-05 GND
PWM13 -> 220 Ohm resistor -> LED -> GND
