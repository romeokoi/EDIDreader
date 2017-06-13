package tko.edidreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    public int fileNumber = 0;
    public Map<String, String> map = new HashMap<String, String>();
    private ListView infoListView;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;
    private String manu_id;
    private String manufacturer;
    private String productCode;
    private String serialNumber;
    private String year;
    private String week;
    public String email;
    public String phoneNumber;
    public String rawedid;

    private Button connectButton;

    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBluetoothAdapter;

    private ConnectedThread mConnectedThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoListView = (ListView) findViewById(R.id.edidInfoListView);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.displayInformation();

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setTag(1);

        SharedPreferences details = getSharedPreferences("details", MODE_PRIVATE);
        SharedPreferences.Editor edt =  details.edit();
        if (details.getString("email","empty").equals("empty")) {
            edt.putString("email", "Email");
            edt.putString("phoneNumber", "Phone Number");
            edt.apply();
        }
        else {
            email = details.getString("email",null);
            phoneNumber = details.getString("phoneNumber",null);
        }

        Log.i("debug",  android.os.Environment.getExternalStorageDirectory().getAbsolutePath());


        //Bluetooth stuff
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("raspberrypi"))
                {
                    Log.e("Raspberry pi",device.getName());
                    mDevice = device;
                    break;
                }
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }
    @Override
    public void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("raspberrypi"))
                {
                    Log.e("Raspberry pi",device.getName());
                    mDevice = device;
                    break;
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mSocket.close();
        } catch (IOException e)  {
            Log.i("error","Cant close socket");
        }
    }
    /* initiates and displays listview with info in HashMap
    * maybe create an object instead of using a map? */
    private void displayInformation(){
        map.put("Manufacturer ID","");
        map.put("Manufacturer","");
        map.put("Manufacturer product code","");
        map.put("Serial Number","");
        map.put("Year of Manufacture","");
        map.put("Week of Manufacture","");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            list.add(key + ": " + value);
        }

        infoListView.setAdapter(adapter);
    }

    /* updates the listview with new info*/
    private void updateInformation() {
        map.put("Manufacturer ID",manu_id);
        map.put("Manufacturer",manufacturer);
        map.put("Manufacturer product code",productCode);
        map.put("Serial Number",serialNumber);
        map.put("Year of Manufacture",year);
        map.put("Week of Manufacture",week);
        list.clear();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            list.add(key + ": " + value);
        }


        infoListView.setAdapter(adapter);
    }

    /* action performed when raw data menu item is pressed*/
    public void showRaw(MenuItem item){
        Log.i("debug","showRaw");
        Intent i = new Intent(this,SecondActivity.class);
        i.putExtra("fragType","raw");
        i.putExtra("rawEDID",rawedid);

        startActivity(i);
    }

    /*action performed when settings menu item is pressed */
    public void showSettings(MenuItem item) {
        Log.i("debug","showSettings");
        Intent i = new Intent(this,SecondActivity.class);
        i.putExtra("fragType","setting");

        startActivity(i);


    }

    /* action when shareButton is clicked */
    public void shareInfo(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("*/*");

        Intent chooser = Intent.createChooser(emailIntent, "Share this CSV file via");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, "example@email.com");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
    /*
        Tag 1 - Connect
        Tag 0 - Disconnect
     */
    public void connectDevice(View view){
        int status = (Integer) view.getTag();
        //"connect" shown
        if ( status == 1) {
            try {

                mSocket = mDevice.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
                if (!mSocket.isConnected()) {
                    mSocket.connect();

                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    connectButton.setText("Disconnect");
                    view.setTag(0);
                }
            } catch (IOException e) {
                try {
                    mConnectedThread.cancel();
                    Toast.makeText(getApplicationContext(), "Can't Connect", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Log.e("debug", "Could not close the client socket", ex);
                }
                Log.e("debug", "cannot connect");
                e.printStackTrace();
                return;
            }
            mConnectedThread = new ConnectedThread(mSocket);
            mConnectedThread.start();

        }
        //"disconnect" clicked
        else {
            try {
                mConnectedThread.cancel();
                Toast.makeText(getApplicationContext(), "Can't Connect", Toast.LENGTH_SHORT).show();
                connectButton.setText("Connect");
                view.setTag(1);
            } catch (Exception ex) {
                Log.e("debug", "Could not close the client socket", ex);
            }

        }



    }
    public void getEDID(View view) {
        String str = "getedid";
        byte[] send = str.getBytes();
        ConnectedThread r;
        if (mConnectedThread != null) {
            // Synchronize a copy of the ConnectedThread
            synchronized (this) {
                r = mConnectedThread;
            }
            Log.e("debug","sending edid");
            r.write(send);
        }
        else {
            Toast.makeText(getApplicationContext(), "connect to raspberry pi first", Toast.LENGTH_SHORT).show();
        }

    }
    public void putinfo(String str){
        try {
            JSONObject jsonObject = new JSONObject(str);
            week = jsonObject.getString("week");
            manufacturer = jsonObject.getString("manufacture_name");
            manu_id = jsonObject.getString("manufacture_id");
            year = jsonObject.getString("year");
            productCode = jsonObject.getString("manufacture_code_hex");
            serialNumber = jsonObject.getString("serial_number_hex");
            rawedid = jsonObject.getString("raw");
            Log.e("debug","parsed json object");

        } catch (JSONException e){
            Log.e("error","can't parse json", e);
        }
        updateInformation();


    }
    /* creates csv file*/
    private void makeFile() {
        try {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            File file = new File(baseDir + "EDIDDisplay" + fileNumber + ".csv");

            if (!file.exists()) {
                file.createNewFile();
            }
            if(file.exists()) {
                try {
                    FileWriter fileWriter  = new FileWriter(file);
                    BufferedWriter bfWriter = new BufferedWriter(fileWriter);
                    bfWriter.write("Text Data");
                    bfWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("error", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("error", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d("Error", "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("Error", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Error", "Could not close the connect socket", e);
            }
        }
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg){
            switch (msg.what) {
                case MessageConstants.MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    String readMsg = new String(readBuf,0,msg.arg1);
                    if (readMsg.equals("not edid")) {
                        Toast.makeText(getApplicationContext(), "Can't get Json", Toast.LENGTH_SHORT).show();
                        Log.e("error", "not edid");
                    }
                    putinfo(readMsg);
                    break;
                case MessageConstants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMsg = new String(writeBuf);

                    break;
                case MessageConstants.MESSAGE_TOAST:
                    break;
            }

        }

    };
    public void disconnected(){
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        connectButton.setText("Connect");
        findViewById(R.id.connectButton).setTag(1);

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
               Log.e("debug","device found");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.e("debug","device connected");
            }

            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
               Log.e("debug","device is about to disconnect");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.e("debug", "device disconnected");
                disconnected();
            }
        }
    };



}



