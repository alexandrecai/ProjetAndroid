package com.example.lesnettoyeurs;

public class Contrat {

    private int cible_id;
    private int value;
    private double lon;
    private double lat;


    public Contrat(int cible_id, int value, double lon, double lat) {
        this.cible_id = cible_id;
        this.value = value;
        this.lon = lon;
        this.lat = lat;
    }

    public int getCible_id() {
        return cible_id;
    }

    public int getValue() {
        return value;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
