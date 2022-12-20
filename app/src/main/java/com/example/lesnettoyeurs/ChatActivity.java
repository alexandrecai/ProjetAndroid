package com.example.lesnettoyeurs;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity implements MessagesFragment.OnListFragmentInteractionListener{

    public static final String TAG = "ChatActivity";
    private String session;
    private String signature;
    private MessagesFragment messagesFragment;
    private List<Message> listMessages;
    private WebServiceLastMSG ws_lastmessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.session = getIntent().getStringExtra("session");
        this.signature = getIntent().getStringExtra("signature");

        messagesFragment = (MessagesFragment) getSupportFragmentManager().findFragmentById(R.id.mainNoteFrag);
        listMessages = new ArrayList<>();

        Button bt_retour = findViewById(R.id.buttonChatRetour);

        new Thread(() -> {
            ws_lastmessages = new WebServiceLastMSG(this.session, this.signature);
        }).start();

        bt_retour.setOnClickListener(view -> {
            finish();
        });

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                raffaichirMessages();
            }
        }, 1000, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        raffaichirMessages();
    }

    @Override
    public void onListFragmentInteraction(Message item) {
        Log.d("NoteCallback",item.getTitre()+" - "+item.getContenu());
    }

    private void raffaichirMessages() {
        Log.d(TAG, "Raffraichissement des messages");
        new Thread(() -> {
            this.listMessages = ws_lastmessages.callWebService();


            try {
                runOnUiThread(() -> {
                    messagesFragment.deleteMessages();

                    for (Message m : this.listMessages)
                    {
                        Date date = new Date(m.getDate());
                        messagesFragment.addMessage(m.getId(),date,m.getTitre(),m.getContenu());

                    }

                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }
}