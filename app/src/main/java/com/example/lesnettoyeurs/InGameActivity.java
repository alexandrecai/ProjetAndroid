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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class InGameActivity extends AppCompatActivity implements LocationListener{

    public static final String TAG = "InGameActivity";

    private String session;
    private String signature;
    private Double actual_lon;
    private Double actual_lat;
    private LocationManager locationManager;
    private WebServiceCreationNettoyeur ws_creation;
    private WebServiceMajPosition ws_majposition;
    private WebServiceModeVoyage ws_modevoyage;
    private WebServiceRemiseEnJeu ws_remiseenjeu;
    private ArrayList<Contrat> availableContractList;
    private ArrayList<NettoyeurEnnemi> ennemisList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_game);

        TextView tv_session = findViewById(R.id.textViewSession);
        TextView tv_signature = findViewById(R.id.textViewSignature);

        this.session = getIntent().getStringExtra("session");
        this.signature = getIntent().getStringExtra("signature");

        this.availableContractList = new ArrayList<Contrat>();
        this.ennemisList = new ArrayList<NettoyeurEnnemi>();

        tv_session.setText(this.session);
        tv_signature.setText(this.signature);
        Button bt_creerNettoyeur = findViewById(R.id.buttonCreationNettoyeur);
        Button bt_modeVoyage = findViewById(R.id.buttonModeVoyage);
        Button bt_remiseenjeu = findViewById(R.id.buttonRemiseEnJeu);

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
            ws_majposition = new WebServiceMajPosition(this.session, this.signature);
            ws_modevoyage = new WebServiceModeVoyage(this.session, this.signature);
            ws_remiseenjeu = new WebServiceRemiseEnJeu(this.session, this.signature);

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    String status = ws_majposition.callWebService(actual_lon, actual_lat, availableContractList, ennemisList);


                    for (Contrat contrat: availableContractList) {
                        Log.d(TAG, "cible_id = " + contrat.getCible_id() + " - value = " + contrat.getValue() + " - lon = " + contrat.getLon() + " - lat = " + contrat.getLat());
                    }

                    for (NettoyeurEnnemi ennemi: ennemisList) {
                        Log.d(TAG, "net_id = " + ennemi.getNet_id() + " - value = " + ennemi.getValue() + " - lon = " + ennemi.getLon() + " - lat = " + ennemi.getLat() + " - lifespan = " + ennemi.getLifespan());
                    }
                }
            }, 2000, 10000);
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

        bt_modeVoyage.setOnClickListener(view -> new Thread(()->{
            if(actual_lon != null && actual_lat != null){
                String status = ws_modevoyage.callWebService();
                this.runOnUiThread(() -> {
                    if(actual_lon != null && actual_lat != null){
                        Toast ts_modeVoyage = Toast.makeText(getApplicationContext(),status,Toast.LENGTH_SHORT);
                        ts_modeVoyage.show();
                    }
                });
            }
        }).start());

        bt_remiseenjeu.setOnClickListener(view -> new Thread(()->{
            if(actual_lon != null && actual_lat != null){
                String status = ws_remiseenjeu.callWebService(actual_lon, actual_lat);
                this.runOnUiThread(() -> {
                    if(actual_lon != null && actual_lat != null){
                        Toast ts_remiseEnJeu = Toast.makeText(getApplicationContext(),status,Toast.LENGTH_SHORT);
                        ts_remiseEnJeu.show();
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
