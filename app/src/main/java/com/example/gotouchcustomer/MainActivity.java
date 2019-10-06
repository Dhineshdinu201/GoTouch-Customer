package com.example.gotouchcustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arvind.otpview.OTPView;
import com.arvind.otpview.OnCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
EditText et_input;
Button btn_submit;
String input;
    String IMEI_Number_Holder;
    TelephonyManager telephonyManager;
    String GET_URL;
OTPView otpView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        et_input=(EditText)findViewById(R.id.et_input);
        btn_submit=(Button)findViewById(R.id.register);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input="";
                input=et_input.getText().toString();
                if(input.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please insert mobile number", Toast.LENGTH_SHORT).show();
                }else
                {
                    api();
                }

            }
        });
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public void showconsultancy(){
        Activity activity = null;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        EditText editText;
        Button close;

        final AlertDialog alertDialog = dialogBuilder.create();
        LayoutInflater factory = LayoutInflater.from(this);
        final View vi = factory.inflate(R.layout.alert_activate_number, null);
        editText=(EditText)vi.findViewById(R.id.ac_code);
        close=(Button)vi.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        alertDialog.setView(vi);
        alertDialog.show();
        alertDialog.setCancelable(false);


    }
    public void api() {
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                Toast.makeText(this, "allow app to read phone state", Toast.LENGTH_SHORT).show();
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            else {
                IMEI_Number_Holder = telephonyManager.getDeviceId();
                Log.i("IMEI",IMEI_Number_Holder);
            }
        }
        GET_URL = "http://103.91.84.218:9006/v1/gotouch/login/user/getData/"+IMEI_Number_Holder;
        Log.i("url",GET_URL);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("My success", "" + response);


                try {
                    //will receive id when the register is success
                    JSONObject js = new JSONObject(response);
                    Intent intent=new Intent(MainActivity.this,MainScreen.class);
                    intent.putExtra("id",input);
                    startActivity(intent);
                    //************parsing response object**********
                } catch (JSONException e) {
                    e.printStackTrace();
                    showconsultancy();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Please check connectivity", Toast.LENGTH_SHORT).show();
                Log.i("My error", "" + error);
            }
        }) {
            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                //send your params here

                return map;
            }
        };
        queue.add(request);

    }
}
