package com.example.localens.models;

import java.util.List;

public class MediaData {
    public final String type;
    public final List<String> tags;
    //Comments?
    //Caption?
    //Likes?
    //User?
    //Created time?
    public final ImageTypes images;
    //Id?
    //Location?

    public MediaData(String type, List<String> tags, ImageTypes images) {
        this.type = type;
        this.tags = tags;
        this.images = images;
    }
}
