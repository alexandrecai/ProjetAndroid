package com.example.lesnettoyeurs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class InGameActivity extends AppCompatActivity {

    private String session;
    private String signature;
    private Double actual_lon;
    private Double actual_lat;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_game);

        TextView tv_session = findViewById(R.id.textViewSession);
        TextView tv_signature = findViewById(R.id.textViewSignature);

        this.session = getIntent().getStringExtra("session");
        this.signature = getIntent().getStringExtra("signature");

        tv_session.setText(this.session);
        tv_signature.setText(this.signature);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        new Thread(() -> {
            WebServiceCreationNettoyeur ws_creation = new WebServiceCreationNettoyeur(this.session,this.signature);
            ws_creation.callWebService(20.2, 10.5);
        }).start();


    }

    public void getLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("testLocation", "ici");

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    Log.d("testLocation", "null");
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        actual_lon = location.getLongitude();
                        actual_lat = location.getLatitude();

                        Log.d("testLocation", "lon = " + actual_lon);
                        Log.d("testLocation", "lat = " + actual_lat);
                    }
                });
    }
}
