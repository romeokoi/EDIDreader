package tko.edidreader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public int fileNumber = 0;
    public Map<String, String> map = new HashMap<String, String>();
    private ListView infoListView;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;
    private String id;
    private String manufacturer;
    private String productCode;
    private String serialNumber;
    private String ym;
    public String email;
    public String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoListView = (ListView) findViewById(R.id.edidInfoListView);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.displayInformation();

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

    /* initiates and displays listview with info in HashMap
    * maybe create an object instead of using a map? */
    private void displayInformation(){
        map.put("Manufacturer ID","");
        map.put("Manufacturer","");
        map.put("Manufacturer product code","");
        map.put("Serial Number","");
        map.put("Year and Month of Manufacture","");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            list.add(key + ": " + value);
        }

        infoListView.setAdapter(adapter);
    }

    /* updates the listview with new info*/
    private void updateInformation() {
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




}



