package com.example.localens;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Instagram {
    @GET("locations/search")
    Call<InstagramApi.SearchResults> searchLocations(
            @Query("lat") String latitude,
            @Query("lng") String longitude,
            @Query("access_token") String accessToken
    );
}
