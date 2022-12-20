package com.example.lesnettoyeurs;

import androidx.annotation.NonNull;

import java.util.Date;

public class Message {
    private final int mId;
    private final String mAuteur;
    private final String mContenu;
    private final Date mDate;

    public Message(int id, @NonNull String auteur, @NonNull String contenu, @NonNull Date date) {
        mId=id;
        mAuteur=auteur;
        mDate=date;
        mContenu=contenu;
    }

    public String getTitre() {
        return mAuteur;
    }

    public String getContenu() {
        return mContenu;
    }

    public String getDate() {
        return mDate.toString();
    }

    public Date getVraieDate() {return mDate;}

    public int getId() {
        return mId;
    }
}
