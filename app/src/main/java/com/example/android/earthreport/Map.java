package com.example.android.earthreport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.example.android.earthreport.fragments.TimelineFragment;
import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.model.EarthQuakes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by root on 11/18/17.
 */

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    public static String TAG = Map.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().setHomeButtonEnabled(true);


        //Retrieve the content view theat renders the map
        setContentView(R.layout.map_activity);

        //Get the SupportMapFragment and request notification
        // when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Bundle bundle = getIntent().getExtras();
        String markType = bundle.getString("MARK_TYPE");

        if (markType.equals("single")) {
            double longitude = bundle.getDouble(TimelineFragment.LONGITUDE);
            double latitude = bundle.getDouble(TimelineFragment.LATITUDE);
            String magnitude = bundle.getString(TimelineFragment.MAGNITUDE);
            String date = bundle.getString(TimelineFragment.DATE);
            String cityName = bundle.getString(TimelineFragment.CITY);

            // Add the marker city, country
            // and move the map camera to the same location
            LatLng location = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(cityName)
                    .snippet(" Magnitude:" + magnitude +
                            " Date:" + DataProvider.getformateDate(new Date(Long.valueOf(date)))));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }

        if (markType.equals("multiple")) {

            ArrayList<EarthQuakes> earthQuakesArrayList = new ArrayList<>();
            earthQuakesArrayList = (ArrayList<EarthQuakes>) bundle.getSerializable(TimelineFragment.DATA);

            for (int a = 0; a < earthQuakesArrayList.size(); a++) {
                LatLng location = new LatLng(earthQuakesArrayList.get(a).getLatitude(), earthQuakesArrayList.get(a).getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(earthQuakesArrayList.get(a).getCityname())
                        .snippet("Magnitude:" + earthQuakesArrayList.get(a).getMagnitude() +
                                " Date:" + DataProvider.getformateDate(
                                new Date(
                                        Long.valueOf(earthQuakesArrayList.get(a).getDate())
                                ))
                        )
                );

            }

            Log.i(TAG, "onMapReady: " + earthQuakesArrayList.size());


        }

    }

}
