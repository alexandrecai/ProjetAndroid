package com.example.lesnettoyeurs;

public class StatsNettoyeur {

    private String name;
    private int value;
    private Double lon;
    private Double lat;
    private String status;

    public StatsNettoyeur(String name, int value, Double lon, Double lat, String status) {
        this.name = name;
        this.value = value;
        this.lon = lon;
        this.lat = lat;
        this.status = status;
    }

    public String getName() {
        return name;
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

    public String getStatus() {
        return status;
    }
}
