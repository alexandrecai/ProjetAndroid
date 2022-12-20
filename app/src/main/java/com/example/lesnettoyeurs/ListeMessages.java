package com.example.lesnettoyeurs;

import java.util.ArrayList;
import java.util.Date;

public class ListeMessages {

    ArrayList<Message> mMessages = new ArrayList<>();

    public ListeMessages()
    {
    }

    public void ajouteMessage(int id, Date date, String author, String message)
    {
        mMessages.add(new Message(id,author,message,date));
    }

    public Message get(int i) {
        return mMessages.get(i);
    }

    public boolean deleteMessageFromIndex(int i) {
        if (i<0 || i>=mMessages.size()) return false;
        mMessages.remove(i);
        return true;
    }

    public int deleteMessageFromId(int id) {
        for (int i=0;i<mMessages.size();i++)
        {
            Message msg = mMessages.get(i);
            if(msg.getId()==id)
            {
                mMessages.remove(i);
                return i;
            }
        }
        return -1;
    }

    public boolean  deleteMessages() {
        if (mMessages.isEmpty()) return false;
        mMessages.clear();
        return true;

    }

    public int size() {
        return mMessages.size();
    }
}