package com.example.lesnettoyeurs;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connexionWebService();
    }

    private void connexionWebService(){
        new Thread(() -> {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
            Log.d("testConnexionInternet ",""+connected);
            if(connected){
                WebServiceConnexion wsConnexion = new WebServiceConnexion();
                wsConnexion.connectToWebService();
            }
        }).start();
    }
}