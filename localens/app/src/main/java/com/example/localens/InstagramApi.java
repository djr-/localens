package com.example.localens;

import java.util.List;

public class InstagramApi {
    public static final String API_URL = "https://api.instagram.com/v1/";

    public static class Location {
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

    public static class SearchResults {
        public final List<Location> data;

        public SearchResults(List<Location> data) {
            this.data = data;
        }
    }

    public static class Image {
        public String url;
        public int width;
        public int height;

        public Image(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }

    public static class ImageTypes {
        public Image low_resolution;
        //Thumbnail?
        //Standard res?

        public ImageTypes(Image low_resolution) {
            this.low_resolution = low_resolution;
        }
    }

    public static class MediaData {
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

    public static class RecentMediaResults {
        public final List<MediaData> data;

        public RecentMediaResults(List<MediaData> data) {
            this.data = data;
        }
    }
}
