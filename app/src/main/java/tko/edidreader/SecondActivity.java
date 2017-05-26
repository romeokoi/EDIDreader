package tko.edidreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SecondActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        RawDataFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("fragType");
        if (message.equals("setting")) {
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment_place,new SettingsFragment()).commit();
        }
        else if (message.equals("raw")) {
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment_place,new RawDataFragment()).commit();
        }
    }

    public void getEmail(View view) {
        EditText edit = (EditText)findViewById(R.id.email_editText);

        SharedPreferences details = getSharedPreferences("details", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt =  details.edit();
        edt.putString("email",edit.getText().toString());
        edt.commit();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public void getPhone(View view) {
        EditText edit = (EditText)findViewById(R.id.phone_editText);

        SharedPreferences details = getSharedPreferences("details", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt =  details.edit();
        String phone = edit.getText().toString();
        edt.putString("phoneNumber",phone);
        edt.commit();
        Log.i("debug",phone);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }


    @Override
    public void onFragmentInteraction(String string){
        //you can leave it empty
    }


}
