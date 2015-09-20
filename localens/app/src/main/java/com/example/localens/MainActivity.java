package com.example.localens;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InstagramApi.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Instagram instagram = retrofit.create(Instagram.class);

        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
        final String accessToken = getResources().getString(R.string.InstagramToken);

        Call<InstagramApi.SearchResults> call = instagram.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken);
        call.enqueue(new Callback<InstagramApi.SearchResults>() {
            @Override
            public void onResponse(Response<InstagramApi.SearchResults> response) {
                InstagramApi.SearchResults searchResults = response.body();

                List<String> locationIDs = new ArrayList<String>(); //TODO: Populate these as location objects rather than simply IDs

                System.out.println("List of found nearby locations:");
                for (InstagramApi.Location location : searchResults.data) {
                    System.out.println(location.name);
                    locationIDs.add(location.id);
                }

                //TODO: The below code should be moved to a better place once it has been tested.
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(InstagramApi.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Instagram instagram = retrofit.create(Instagram.class);

                Call<InstagramApi.RecentMediaResults> call = instagram.recentMedia(locationIDs.get(0), accessToken);
                call.enqueue(new Callback<InstagramApi.RecentMediaResults>() {
                    @Override
                    public void onResponse(Response<InstagramApi.RecentMediaResults> response) {
                        InstagramApi.RecentMediaResults searchResults = response.body();
                        System.out.println("START");
                        System.out.println(searchResults.data.get(0).type);
                        System.out.println("END");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }


                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
