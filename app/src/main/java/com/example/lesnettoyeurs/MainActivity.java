package com.example.lesnettoyeurs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String session;
    private String signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        Button button_connexion = findViewById(R.id.buttonConnexion);
        EditText editText_login = findViewById(R.id.editTextLogin);
        EditText editText_password = findViewById(R.id.editTextPassword);

        button_connexion.setOnClickListener(v ->{
            connexionWebService(editText_login.getText().toString(),editText_password.getText().toString());
        });



    }

    private void connexionWebService(String login, String password){
        new Thread(() -> {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
            Log.d("testConnexionInternet ",""+connected);
            if(connected){
                WebServiceConnexion wsConnexion = new WebServiceConnexion(login,password);
                Map<String,String> identifiants_connexion = wsConnexion.connectToWebService();
                this.session = identifiants_connexion.get("session");
                this.signature = identifiants_connexion.get("signature");
                if(identifiants_connexion.get("status").equals("KO - WRONG CREDENTIALS")){
                    this.runOnUiThread(() -> {
                        Toast toast_wc = Toast.makeText(getApplicationContext(),"Login ou mot de passe incorrect", Toast.LENGTH_SHORT);
                        toast_wc.show();
                    });
                }
                else if(identifiants_connexion.get("status").equals("KO-technical error")){
                    this.runOnUiThread(() -> {
                        Toast toast_te = Toast.makeText(getApplicationContext(),"Erreur technique", Toast.LENGTH_SHORT);
                        toast_te.show();
                    });
                }
                else{
                    Intent intent = new Intent(this,InGameActivity.class);
                    intent.putExtra("session",this.session);
                    intent.putExtra("signature",this.signature);
                    startActivity(intent);
                }
            }
        }).start();


    }
}