package com.example.localens;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private Toast _toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

                List<InstagramApi.Location> locations = new ArrayList<InstagramApi.Location>();

                System.out.println("List of found nearby locations:");
                for (InstagramApi.Location location : searchResults.data) {
                    System.out.println(location.name);
                    locations.add(location);
                }

                //TODO: The below code should be moved to a better place once it has been tested.
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(InstagramApi.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Instagram instagram = retrofit.create(Instagram.class);

                System.out.println("Pulling data from " + locations.get(0).name);
                _toast.setText(locations.get(0).name);
                _toast.show();

                Call<InstagramApi.RecentMediaResults> call = instagram.recentMedia(locations.get(0).id, accessToken);
                call.enqueue(new Callback<InstagramApi.RecentMediaResults>() {
                    @Override
                    public void onResponse(Response<InstagramApi.RecentMediaResults> response) {
                        InstagramApi.RecentMediaResults mediaResults = response.body();
                        for (InstagramApi.MediaData mediaData : mediaResults.data) {
                            System.out.println(mediaData.images.standard_resolution.url);
                            Picasso.with(getApplicationContext()).load(mediaData.images.standard_resolution.url).into((ImageView) findViewById(R.id.only_image));
                        }
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
