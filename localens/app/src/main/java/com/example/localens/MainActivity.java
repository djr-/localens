package com.example.localens;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.localens.models.Location;
import com.example.localens.models.LocationSearchResults;
import com.example.localens.models.MediaData;
import com.example.localens.models.RecentMediaSearchResults;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity {

    private Toast _toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //TODO: Force an update of the GPS onCreate.
        final android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);

        final String accessToken = getResources().getString(R.string.InstagramToken);

        InstagramService instagramService = InstagramService.Factory.create();

        Call<LocationSearchResults> call = instagramService.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken);
        call.enqueue(new Callback<LocationSearchResults>() {
            @Override
            public void onResponse(Response<LocationSearchResults> response) {
                System.out.println(response);
                LocationSearchResults searchResults = response.body();

                List<Location> locations = new ArrayList<Location>();

                System.out.println("List of found nearby locations:");
                for (Location location : searchResults.data) {
                    System.out.println(location.name);
                    locations.add(location);
                }

                System.out.println("Pulling data from " + locations.get(0).name);
                _toast.setText(locations.get(0).name);
                _toast.show();

                Call<RecentMediaSearchResults> call = InstagramService.Factory.create().recentMedia(locations.get(0).id, accessToken);
                call.enqueue(new Callback<RecentMediaSearchResults>() {
                    PhotoViewAttacher photoViewAttacher;

                    @Override
                    public void onResponse(Response<RecentMediaSearchResults> response) {
                        RecentMediaSearchResults mediaResults = response.body();
                        for (MediaData mediaData : mediaResults.data) {
                            System.out.println(mediaData.images.standard_resolution.url);

                            ImageView imageView = (ImageView) findViewById(R.id.only_image);
                            photoViewAttacher = new PhotoViewAttacher(imageView);
                            Picasso.with(getApplicationContext()).load(mediaData.images.standard_resolution.url).into(imageView);
                            photoViewAttacher.update();
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

        //Subscription subscription;
        //subscription = instagramService.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken);

        //Observable<InstagramApi.SearchResults> call = instagramService.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken);

//        instagramService.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken)
//                .subscribe(new Subscriber<InstagramApi.SearchResults>() {
//
//                    @Override
//                    public void onCompleted() {
//                        System.out.println("COMPLETED!");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        System.out.println("ERROR!");
//                    }
//
//                    @Override
//                    public void onNext(InstagramApi.SearchResults searchResults) {
//                        System.out.println("NEXT!");
//                    }
//                });
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
