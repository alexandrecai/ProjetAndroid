package com.example.lesnettoyeurs;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InGameActivity extends AppCompatActivity {

    private String session;
    private String signature;

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

        new Thread(() -> {
            WebServiceCreationNettoyeur ws_creation = new WebServiceCreationNettoyeur(this.session,this.signature);
            ws_creation.callWebService();
        }).start();

    }
}
