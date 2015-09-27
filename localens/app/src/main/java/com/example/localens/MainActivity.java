package com.example.localens;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.localens.models.LocationSearchResults;
import com.example.localens.models.RecentMediaSearchResults;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

        final InstagramService instagramService = InstagramService.Factory.create();

        instagramService.searchLocations(Double.toString(latitude), Double.toString(longitude), accessToken)
            .flatMap(new Func1<LocationSearchResults, Observable<RecentMediaSearchResults>>() {
                @Override
                public Observable<RecentMediaSearchResults> call(LocationSearchResults locationSearchResults) {
                    _toast.setText(locationSearchResults.data.get(0).name);
                    _toast.show();
                    return instagramService.recentMedia(locationSearchResults.data.get(0).id, accessToken);
                }
            })
            .map(new Func1<RecentMediaSearchResults, String>() {
                @Override
                public String call(RecentMediaSearchResults recentMediaSearchResults) {
                    return recentMediaSearchResults.data.get(0).images.standard_resolution.url;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    PhotoViewAttacher photoViewAttacher;
                    ImageView imageView = (ImageView) findViewById(R.id.only_image);
                    photoViewAttacher = new PhotoViewAttacher(imageView);
                    Picasso.with(getApplicationContext()).load(s).into(imageView);
                    photoViewAttacher.update();
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
