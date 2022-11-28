package com.example.lesnettoyeurs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_game);

        TextView tv_session = findViewById(R.id.textViewSession);
        TextView tv_signature = findViewById(R.id.textViewSignature);

        tv_session.setText(getIntent().getStringExtra("session"));
        tv_signature.setText(getIntent().getStringExtra("signature"));

    }
}
