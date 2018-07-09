package com.example.jake_jay_p.datalabellerprototype;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private static final String LOG_TAG = LoadingActivity.class.getSimpleName();
    public static final String EXTRA_REPLY = "com.example.jake_jay_p.datalabellerprototype";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // ---- Get permission to access (and then retrieve) the device IMEI
        String IMEI = loadIMEI();

        // ---- Register the users ID
        registerIMEI( IMEI );

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, IMEI);
        setResult(RESULT_OK, replyIntent);
        finish();

    }

    public void registerIMEI(String IMEI){

        JSONObject toPost = new JSONObject();                   // The data that is sent to the API
        try {
            toPost.put("device_number", IMEI);
        } catch (JSONException e) {
            Log.d( LOG_TAG, "Error creating JSON object: " + e );
        }

        // ---- If there is data to post and the patternID is not 0 (zero is the default and means that the connection is not working)
        if (toPost.length() > 0 ) {
            Log.d(LOG_TAG, "Try register a user with IMEI: " + IMEI);
            new RegisterUser().execute( String.valueOf(toPost) );    // Add a label
        }

    }


    public String loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            // ---- READ_PHONE_STATE permission has not been granted.
            Log.d(LOG_TAG, "Requesting Permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

            TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            // ---- Get IMEI Number of Phone
            return tm.getDeviceId();
        } else {
            // ---- READ_PHONE_STATE permission has already been granted. Simply get the IMEI
            Log.d(LOG_TAG, "Permission has already been granted");

            TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }
    }

}
