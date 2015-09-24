package com.example.localens;

import com.example.localens.models.LocationSearchResults;
import com.example.localens.models.RecentMediaSearchResults;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface InstagramService {
    public static final String API_BASE_URL = "https://api.instagram.com/v1/";

    //Observable<InstagramApi.SearchResults> searchLocations(
    @GET("locations/search")
    Call<LocationSearchResults> searchLocations(
            @Query("lat") String latitude,
            @Query("lng") String longitude,
            //@Query("distance") String distance,   //Optional parameter -- currently unused.
            @Query("access_token") String accessToken
    );

    @GET("locations/{location_id}/media/recent")
    Call<RecentMediaSearchResults> recentMedia(
            @Path("location_id") String locationId,
            @Query("access_token") String accessToken
    );

    class Factory {
        public static InstagramService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            return retrofit.create(InstagramService.class);
        }
    }
}
