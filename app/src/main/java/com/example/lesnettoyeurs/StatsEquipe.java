package com.example.lesnettoyeurs;

public class StatsEquipe {

    private int value;
    private int adv_value;
    private int active_members;

    public StatsEquipe(int value, int adv_value, int active_members) {
        this.value = value;
        this.adv_value = adv_value;
        this.active_members = active_members;
    }

    public int getValue() {
        return value;
    }

    public int getAdv_value() {
        return adv_value;
    }

    public int getActive_members() {
        return active_members;
    }
}
