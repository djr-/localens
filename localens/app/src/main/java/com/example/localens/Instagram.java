package com.example.localens;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Instagram {
    @GET("locations/search")
    Call<InstagramApi.SearchResults> searchLocations(
            @Query("lat") String latitude,
            @Query("lng") String longitude,
            //@Query("distance") String distance,   //Optional parameter -- currently unused.
            @Query("access_token") String accessToken
    );

    @GET("locations/{location_id}/media/recent")
    Call<InstagramApi.RecentMediaResults> recentMedia(
            @Path("location_id") String locationId,
            @Query("access_token") String accessToken
    );
}
