package com.example.lesnettoyeurs;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class InGameActivity extends AppCompatActivity implements LocationListener{

    private String session;
    private String signature;
    private Double actual_lon;
    private Double actual_lat;
    private LocationManager locationManager;
    private WebServiceCreationNettoyeur ws_creation;



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
        Button bt_creerNettoyeur = findViewById(R.id.buttonCreationNettoyeur);

        if (ContextCompat.checkSelfPermission(InGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(InGameActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION

            },100);
        }

        if (ContextCompat.checkSelfPermission(InGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }




        new Thread(() -> {
            ws_creation = new WebServiceCreationNettoyeur(this.session, this.signature);
        }).start();

        
        bt_creerNettoyeur.setOnClickListener(view -> new Thread(()->{

            if(actual_lon != null && actual_lat != null){
                String status = ws_creation.callWebService(actual_lon, actual_lat);
                this.runOnUiThread(() -> {
                    if(actual_lon != null && actual_lat != null){
                        Toast ts_creerNettoyeur = Toast.makeText(getApplicationContext(),status,Toast.LENGTH_SHORT);
                        ts_creerNettoyeur.show();
                    }
                });
            }
        }).start());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(InGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "resume");
        if (ContextCompat.checkSelfPermission(InGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }




    // ##################################### Geolocalisation #####################################

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,InGameActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        actual_lat = location.getLatitude();
        actual_lon = location.getLongitude();
        Log.d("testLocation", "Lat = " + actual_lat + " Lng = " + actual_lon);
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
