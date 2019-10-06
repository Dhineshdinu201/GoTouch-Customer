package com.example.gotouchcustomer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import pl.droidsonroids.gif.GifImageView;

public class MainScreen extends AppCompatActivity {
    TextView et_name, et_contact, et_model, et_imei, et_valid;
    String id;
    GifImageView loading;
    LinearLayout lin;
    String IMEI_Number_Holder;
    TelephonyManager telephonyManager;
    String GET_URL;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        et_name = (TextView) findViewById(R.id.name);
        lin = (LinearLayout) findViewById(R.id.lin);
        et_contact = (TextView) findViewById(R.id.contact);
        et_model = (TextView) findViewById(R.id.mobile_model);
        et_imei = (TextView) findViewById(R.id.imei);
        et_valid = (TextView) findViewById(R.id.valid);
        loading = (GifImageView) findViewById(R.id.loading);
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
                Toast.makeText(this, ""+IMEI_Number_Holder, Toast.LENGTH_SHORT).show();
            }
        }

        try {

            id = getIntent().getStringExtra("id");
            Log.i("enId", id);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    api();
                }
            }, SPLASH_DISPLAY_LENGTH);


        } catch (NullPointerException e) {
            Log.i("enId", "noId");
            e.printStackTrace();
        }

    }
    public void api() {
        GET_URL = "http://103.91.84.218:9006/v1/gotouch/login/user/getData/"+id;
        Log.i("url",GET_URL);
        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);
        StringRequest request = new StringRequest(Request.Method.GET, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("My success", "" + response);


                try {
                    //will receive id when the register is success
                    JSONObject js = new JSONObject(response);
                    String name=js.getString("name");
                    String contact=js.getString("contact_number");
                    String model=js.getString("mobile_model");
                    String imei=js.getString("imei");
                    String valid=js.getString("valid");
                    et_name.setText(name);
                    et_contact.setText(contact);
                    et_model.setText(model);
                    et_imei.setText(imei);
                    et_valid.setText(valid);

                    lin.setVisibility(View.GONE);
                    //************parsing response object**********
                } catch (JSONException e) {
                    e.printStackTrace();
                    lin.setVisibility(View.GONE);
                    Toast.makeText(MainScreen.this, "Somewhere went wrong", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainScreen.this, "Please check connectivity", Toast.LENGTH_SHORT).show();
                Log.i("My error", "" + error);
                lin.setVisibility(View.GONE);
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

