# EDIDreader 
An Android Application that gets parsed EDID display information connected to a raspberry pi. User connects to a raspberry pi running the BLServer.py script and gets EDID info from raspberry pi, which parses the data and sends it to the phone via bluetooth. <br>

Note: Manufacture Code/Model Id and Serial Number are in hex<br>
## Installation from Source - if you dont have the signed .apk file
To install the latest development version from source:
```bash
mkdir -p ~/EDIDreader && cd ~/EDIDreader
git clone https://github.com/tko22/EDIDreader.git
```
1. Open Android Studio and open from source ~/edidreader <br>
2. create a run configuration of android app and everything as default <br>
3. Run the app on the phone <br>

For more information on running the app: https://developer.android.com/training/basics/firstapp/running-app.html

### Requirements for installation
- Android Studio 2.3 https://developer.android.com/studio/index.html<br>
- Android Phone with Android 6.0/Android API 23 or above with USB debugging on in developer options <br>

### raspberryPiScript/BLServer.py
this python script must be ran in the raspberry pi. It acts as a bluetooth server and continuously searches for a connection with the android phone. Note: you must be paired with the raspberry pi before you can connect to it. The script will get the EDID display data from the raspberry pi and parse information from the byte file and send it to the phone upon requested. 


## Raspberry Pi Configuration - as of Linux raspberrypi 4.9.31+ #1005
To perform a headless setup of Raspberry pi zero w: http://blog.remmelt.com/2017/03/20/easy-headless-setup-for-raspberry-pi-zero-w-on-osx/<br>
 
Then install image to sd card: https://www.raspberrypi.org/documentation/installation/installing-images/README.md<br>
```bash
ssh pi@raspberrypi.local
pi@raspberrypi.local's password: rasbperry
```
Update and upgrade installed packages:
```bash
sudo apt-get update
sudo apt-get upgrade
```
Install pybluez for python bluetooth functionality:
```bash
sudo apt-get install python-bluez
```

Setup bluetooth for raspberry pi:<br>
```bash
sudo apt-get install pi-bluetooth
sudo reboot
sudo service bluetooth status 
```
status should say “Running”:<br>

To permanently set the bluetooth name of the pi to "raspberrypi":
```bash
sudo nano /etc/machine-info
```
And in it write:
```bash
PRETTY_HOSTNAME=raspberrypi
```
exit out and Save by Control-x, Y, Enter<br>
if you don't want to change name you must change the line in MainActivity:
```java
if(device.getName().equals("raspberrypi"))
``` 
from "raspberrypi" to whatever name<br><br>

We will now pair your android device with the raspberry pi:
```bash
bluetoothctl
```
[bluetooth]# will lead the line
```bash
agent on
default-agent
scan on
discoverable on
```
 
The pi will start scanning, look for your android phone’s name. Note: Your android device must have bluetooth discoverable “on”
 
When the devices pop up on either the android device of raspberry pi:
```bash 
pair [MAC ADDRESS YOU COPY FROM THE SCAN]
``` 
OR you click the “raspberrypi” on your android device
 
A popup will come up on your android device and you click yes.<br>
On your pi, type "yes" to pair
```bash
trust [MAC ADDRESS YOU COPY]
scan off
exit
```
 
Assuming you have setup the git repository: https://github.com/tko22/EDIDreader<br>
BLServer.py is the python script running on the raspberry pi that connects to the android device and parses and sends the EDID information: we shall move it to the home directory of the user pi<br>
 
From the computer you have setup the repository on:
```bash 
scp ~/EDIDreader/raspberryPiScript/BLServer.py pi@raspberrypi.local:~/
```
 
Make script execute on start:
```bash
sudo nano /etc/rc.local
```
Right on top of exit 0 write: python /home/pi/BLserver.py &
Exit out and save: Control-X, Y, Enter
 
### About

#### Author
Timothy Ko - tk2@illinois.edu
 
 
