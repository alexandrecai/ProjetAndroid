package com.example.lesnettoyeurs;

public class NettoyeurEnnemi {

    private int net_id;
    private int value;
    private Double lon;
    private Double lat;
    private int lifespan;

    public NettoyeurEnnemi(int net_id, int value, Double lon, Double lat, int lifespan) {
        this.net_id = net_id;
        this.value = value;
        this.lon = lon;
        this.lat = lat;
        this.lifespan = lifespan;
    }

    public int getNet_id() {
        return net_id;
    }

    public int getValue() {
        return value;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public int getLifespan() {
        return lifespan;
    }
}
