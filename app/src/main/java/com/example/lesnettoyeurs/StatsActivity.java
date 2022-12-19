package com.example.lesnettoyeurs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private String session;
    private String signature;
    private WebServiceStatsNettoyeur ws_statsnettoyeur;
    private WebServiceStatsEquipe ws_statsequipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.stats);

        this.session = getIntent().getStringExtra("session");
        this.signature = getIntent().getStringExtra("signature");

        TextView tv_name = findViewById(R.id.textViewStatsNameValue);
        TextView tv_value = findViewById(R.id.textViewStatsValueValue);
        TextView tv_lon = findViewById(R.id.textViewStatsLonValue);
        TextView tv_lat = findViewById(R.id.textViewStatsLatValue);
        TextView tv_status = findViewById(R.id.textViewStatsStatusValue);

        TextView tv_valueEquipe = findViewById(R.id.textViewStatsEquipeValValue);
        TextView tv_advValue = findViewById(R.id.textViewStatsAdvValueValue);
        TextView tv_activeMembers = findViewById(R.id.textViewStatsActiveMembersValue);

        Button bt_retour = findViewById(R.id.buttonStatsRetour);



        new Thread(() ->{
            ws_statsnettoyeur = new WebServiceStatsNettoyeur(this.session,this.signature);
            StatsNettoyeur statsNettoyeur = ws_statsnettoyeur.callWebService();

            ws_statsequipe = new WebServiceStatsEquipe(this.session,this.signature);
            StatsEquipe statsEquipe = ws_statsequipe.callWebService();

            this.runOnUiThread(() -> {
                tv_name.setText(statsNettoyeur.getName());
                tv_value.setText(""+statsNettoyeur.getValue());
                tv_lon.setText(statsNettoyeur.getLon().toString());
                tv_lat.setText(statsNettoyeur.getLat().toString());
                tv_status.setText(statsNettoyeur.getStatus());

                tv_valueEquipe.setText(""+statsEquipe.getValue());
                tv_advValue.setText(""+statsEquipe.getAdv_value());
                tv_activeMembers.setText(""+statsEquipe.getActive_members());
            });



        }).start();

        bt_retour.setOnClickListener(view -> {
           finish();
        });

    }
}
