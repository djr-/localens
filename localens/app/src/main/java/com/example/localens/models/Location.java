package com.example.localens.models;

public class Location{
    public final String id;
    public final double latitude;
    public final double longitude;
    public final String name;

    public Location(String id, double latitude, double longitude, String name) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }
}
