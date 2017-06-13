# EDIDreader 
An Android Application that gets parsed EDID displah information connected to a raspberry pi. User connects to a raspberry pi running the BLServer.py script and gets EDID info from raspberry pi, which parses the data and sends it to the phone. 

## Installation from Source 
To install the latest development version from source:
```bash
mkdir -p ~/EDIDreader && cd ~/EDIDreader
git clone git@github.com:tko22/EDIDreader.git .
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
