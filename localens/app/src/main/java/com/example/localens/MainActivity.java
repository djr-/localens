package com.example.localens;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Toast _toast;
    private GoogleApiClient _googleApiClient;
    private android.location.Location _lastLocation;

    private synchronized GoogleApiClient buildGoogleApiClient() {
        _googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return _googleApiClient;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient().connect();

        _toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
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

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("onConnected!");
        _lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                _googleApiClient);
        if (_lastLocation != null) {
            displayLatestInstagramPhoto(_lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed!");
    }

    private void displayLatestInstagramPhoto(android.location.Location currentLocation) {
        final InstagramService instagramService = InstagramService.Factory.create();
        final String accessToken = getResources().getString(R.string.InstagramToken);
        final int distanceToCheck = 5000;    // Maximum distance as specified in the Instagram API.

        System.out.println("Current latitude: " + currentLocation.getLatitude());
        System.out.println("Current longitude: " + currentLocation.getLongitude());
        System.out.println("Current location accuracy: " + currentLocation.getAccuracy());

        //TODO: Investigate adding retrolambda to clean up these calls.
        instagramService.searchLocations(Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()), Integer.toString(distanceToCheck), accessToken)
                .map(new Func1<LocationSearchResults, List<Location>>() {
                    @Override
                    public List<Location> call(LocationSearchResults locationSearchResults) {
                    // TODO: This is a very inefficient way to get the sorted list of locations, but it will get the job done for now.
                    ArrayList<Location> locations = new ArrayList<Location>();
                    for (Location location : locationSearchResults.data)    //TODO: It appears that Instagram is only returning a maximum of 20 nearby locations. Need to look into strategies to deal with pagination.
                    {
                        System.out.println(location.name);
                        locations.add(location);
                    }

                    Collections.sort(locations, new Comparator<Location>() {
                        @Override
                        public int compare(Location lhs, Location rhs) {
                            //System.out.println("LHS: " + lhs.name);
                            //System.out.println("RHS: " + rhs.name);
                            android.location.Location lhsLocation = new android.location.Location("");
                            android.location.Location rhsLocation = new android.location.Location("");
                            lhsLocation.setLatitude(lhs.latitude);
                            lhsLocation.setLongitude(lhs.longitude);
                            rhsLocation.setLatitude(rhs.latitude);
                            rhsLocation.setLongitude(rhs.longitude);

                            //System.out.println("LHS distance to current location: " + lhsLocation.distanceTo(_lastLocation));
                            //System.out.println("RHS distance to current location: " + rhsLocation.distanceTo(_lastLocation));
                            return ((Float) lhsLocation.distanceTo(_lastLocation)).compareTo((Float) rhsLocation.distanceTo(_lastLocation));
                        }
                    });

                    return locations;
                    }
                })
                .flatMap(new Func1<List<Location>, Observable<RecentMediaSearchResults>>() {
                    @Override
                    public Observable<RecentMediaSearchResults> call(List<Location> nearbyLocations) {
                        _toast.setText(nearbyLocations.get(0).name);
                        _toast.show();
                        return instagramService.recentMedia(nearbyLocations.get(0).id, accessToken);
                    }
                })
                .map(new Func1<RecentMediaSearchResults, List<MediaData>>() {
                    @Override
                    public List<MediaData> call(RecentMediaSearchResults recentMediaSearchResults) {
                        return recentMediaSearchResults.data;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MediaData>>() {
                    @Override
                    public void call(List<MediaData> mediaData) {
                        PhotoViewAttacher photoViewAttacher;
                        ImageView imageView = (ImageView) findViewById(R.id.only_image);
                        photoViewAttacher = new PhotoViewAttacher(imageView);
                        Picasso.with(getApplicationContext()).load(mediaData.get(0).images.standard_resolution.url).into(imageView);
                        photoViewAttacher.update();
                    }
                });
    }
}