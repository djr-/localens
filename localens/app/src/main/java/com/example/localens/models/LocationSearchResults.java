package com.example.localens.models;

import java.util.List;

public class LocationSearchResults {
    public final List<Location> data;  //    TODO: Use getters/setters instead of making everything public.

//    public List<Location> getData() {
//        return data;
//    }

    public LocationSearchResults(List<Location> data)
    {
        this.data = data;
    }
}
