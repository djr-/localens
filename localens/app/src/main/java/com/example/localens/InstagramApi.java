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

    public static class MediaData {
        public final String type;

        public MediaData(String type) {
            this.type = type;
        }
    }

    public static class RecentMediaResults {
        public final List<MediaData> data;

        public RecentMediaResults(List<MediaData> data) {
            this.data = data;
        }
    }
}
