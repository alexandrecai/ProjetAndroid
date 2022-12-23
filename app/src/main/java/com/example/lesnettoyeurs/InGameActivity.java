package com.example.lesnettoyeurs;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;
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
    private WebServiceNettoyageCible ws_nettoyageCible;
    private WebServiceNettoyageEnnemi ws_nettoyageEnnemi;
    private ArrayList<NettoyeurEnnemi> ennemisList;
    private ArrayList<Contrat> availableContractList;

    protected MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_game);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


        this.session = getIntent().getStringExtra("session");
        this.signature = getIntent().getStringExtra("signature");

        this.availableContractList = new ArrayList<Contrat>();
        this.ennemisList = new ArrayList<NettoyeurEnnemi>();

        Button bt_creerNettoyeur = findViewById(R.id.buttonCreationNettoyeur);
        ImageButton bt_modeVoyage = findViewById(R.id.buttonModeVoyage);
        ImageButton bt_remiseenjeu = findViewById(R.id.buttonRemiseEnJeu);
        ImageButton bt_stats = findViewById(R.id.buttonStats);
        ImageButton bt_chat = findViewById(R.id.buttonChat);

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

        // add options to the map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        // default zoom
        IMapController mapController = map.getController();
        mapController.setZoom(17.0);

        // center map
        GeoPoint centerPoint = new GeoPoint(47.8437, 1.9344);
        mapController.setCenter(centerPoint);

        // min & max zoom
        map.setMinZoomLevel(17.0);
        map.setMaxZoomLevel(19.0);

        // enable rotation
        RotationGestureOverlay rotation = new RotationGestureOverlay(ctx, map);
        rotation.setEnabled(true);
        map.getOverlays().add(rotation);

        //temp
        List<Overlay> myOverlays = map.getOverlays();
        Log.d(TAG, "nb overlays : " + myOverlays.size());
        for(Overlay overlay: myOverlays){
            Log.d(TAG, overlay.toString());
        }



        new Thread(() -> {
            ws_creation = new WebServiceCreationNettoyeur(this.session, this.signature);
            ws_majposition = new WebServiceMajPosition(this.session, this.signature);
            ws_modevoyage = new WebServiceModeVoyage(this.session, this.signature);
            ws_remiseenjeu = new WebServiceRemiseEnJeu(this.session, this.signature);
            ws_nettoyageCible = new WebServiceNettoyageCible(this.session, this.signature);
            ws_nettoyageEnnemi = new WebServiceNettoyageEnnemi(this.session, this.signature);

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(actual_lon != null && actual_lat != null && ContextCompat.checkSelfPermission(InGameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){
                        String status = ws_majposition.callWebService(actual_lon, actual_lat, availableContractList, ennemisList);


                        for (Contrat contrat: availableContractList) {
                            Log.d(TAG, "cible_id = " + contrat.getCible_id() + " - value = " + contrat.getValue() + " - lon = " + contrat.getLon() + " - lat = " + contrat.getLat());
                        }

                        for (NettoyeurEnnemi ennemi: ennemisList) {
                            Log.d(TAG, "net_id = " + ennemi.getNet_id() + " - value = " + ennemi.getValue() + " - lon = " + ennemi.getLon() + " - lat = " + ennemi.getLat() + " - lifespan = " + ennemi.getLifespan());
                        }

                        //delete old markers
                        List<Overlay> overlays = map.getOverlays();
                        Log.d(TAG, "nb overlays : " + overlays.size());
                        int removerdMarkers = 0; // temp
                        for (Overlay overlay: overlays){
                            if(overlay.getClass().equals(Marker.class)){
                                overlays.remove(overlay);
                                removerdMarkers++;
                            }
                        }
                        Log.d(TAG, "rm markers : " + removerdMarkers);

                        displayContractOnMap(map,availableContractList);
                        displayEnnemiesOnMap(map,ennemisList);
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

        bt_stats.setOnClickListener(view -> {
            Intent intent = new Intent(this,StatsActivity.class);
            intent.putExtra("session",this.session);
            intent.putExtra("signature",this.signature);
            startActivity(intent);
        });

        bt_chat.setOnClickListener(view -> {
            Intent intent = new Intent(this,ChatActivity.class);
            intent.putExtra("session",this.session);
            intent.putExtra("signature",this.signature);
            startActivity(intent);
        });

    }


    private void displayContractOnMap(MapView map, ArrayList<Contrat> contrats){

        for (Contrat contrat: contrats){
            Marker marker = new Marker(map);
            marker.setDraggable(false);
            marker.setPosition(new GeoPoint(contrat.getLat(), contrat.getLon()));
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                new Thread(() -> {
                    String status = ws_nettoyageCible.callWebService(String.valueOf(contrat.getCible_id()));
                    Log.d(TAG, "Status nettoyage Cible : " + status);
                }).start();
                return false;
            });
            map.getOverlays().add(marker);
        }
    }

    private void displayEnnemiesOnMap(MapView map, ArrayList<NettoyeurEnnemi> ennemies){

        for (NettoyeurEnnemi ennemie: ennemies){
            Marker marker = new Marker(map);
            marker.setDraggable(false);
            marker.setPosition(new GeoPoint(ennemie.getLat(), ennemie.getLon()));
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                new Thread(() -> {
                    String status = ws_nettoyageEnnemi.callWebService(String.valueOf(ennemie.getNet_id()));
                    Log.d(TAG, "Status nettoyage Ennemi : " + status);
                }).start();
                return false;
            });
            map.getOverlays().add(marker);
        }
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
