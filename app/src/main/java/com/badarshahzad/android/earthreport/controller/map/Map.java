package com.badarshahzad.android.earthreport.controller.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.badarshahzad.android.earthreport.R;
import com.badarshahzad.android.earthreport.controller.adapters.EarthQuakeAdapter;
import com.badarshahzad.android.earthreport.controller.timeline.TimelineFragment;
import com.badarshahzad.android.earthreport.model.pojos.EarthQuakes;
import com.badarshahzad.android.earthreport.model.utilties.DataProviderFormat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

    private SupportMapFragment mapFragment;

    private TextView time;
    private TextView city;
    private TextView title;
    private TextView direction;
    private TextView longitude;
    private TextView magnitude;
    private CardView cardView;

//    private AdView adView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve the content view theat renders the map
        setContentView(R.layout.map_activity);

//        adView = findViewById(R.id.adView);

//        AdRequest adRequest1 = new AdRequest.Builder().build();
//        adView.loadAd(adRequest1);

        getSupportActionBar().setHomeButtonEnabled(true);

        //Get the SupportMapFragment and request notification
        //When the map is ready to be used
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Find the view referance link
        time = findViewById(R.id.time);
        city = findViewById(R.id.city);
        direction = findViewById(R.id.directionDistance);
        longitude = findViewById(R.id.longitude);
        title = findViewById(R.id.title);
        magnitude = findViewById(R.id.magnitude);
        cardView = findViewById(R.id.cardForText);
//        Log.i(TAG, "onCreate: ");

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
            cardView.setVisibility(View.VISIBLE);
            double longitude = bundle.getDouble(TimelineFragment.LONGITUDE);
            double latitude = bundle.getDouble(TimelineFragment.LATITUDE);
            String magnitude = bundle.getString(TimelineFragment.MAGNITUDE);
            String date = bundle.getString(TimelineFragment.DATE);
            String title = bundle.getString(TimelineFragment.CITY);

            String[] parseValue = title.split("(?<=of)");
            String distance = parseValue[0];
            String cityName = parseValue[1];


            // Add the marker city, country
            // and move the map camera to the same location
            LatLng location = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(cityName)
                    .snippet("Magnitude:" + magnitude));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

            //TODO:Add listener and code
           // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
           // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
           // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //I know that this hard coded but the USGS mantain data with that string :D
            // So, Honesty don't know how to get the name of city  the don't have method
            // in API to get the name of city so i'm doing like this

            city.setText(cityName);
            //date parameter take long value as this long value is like key that
            //consist of time and date stamp so only these classes know how to parse them
            time.setText(EarthQuakeAdapter.formateTime(new Date(Long.valueOf(date))));
            this.direction.setText(distance);
            this.longitude.setText(longitude + " , " + latitude);
            this.magnitude.setText(magnitude);
            this.title.setText(title);


//            Log.i(TAG, "onMapReady: " + cityName);
//            Log.i(TAG, "onMapReady: " + date);

        }

        if (markType.equals("multiple")) {
            cardView.setVisibility(View.GONE);

            ArrayList<EarthQuakes> earthQuakesArrayList = earthQuakesArrayList = (ArrayList<EarthQuakes>) bundle.getSerializable(TimelineFragment.DATA);

            for (int a = 0; a < earthQuakesArrayList.size(); a++) {
                LatLng location = new LatLng(earthQuakesArrayList.get(a).getLatitude(), earthQuakesArrayList.get(a).getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(earthQuakesArrayList.get(a).getCityname())
                        .snippet("Magnitude:" + earthQuakesArrayList.get(a).getMagnitude() +
                                " Date:" + DataProviderFormat.getformateDate(
                                new Date(
                                        Long.valueOf(earthQuakesArrayList.get(a).getTimeStamp())
                                ))
                        )
                );

            }


//            Log.i(TAG, "onMapReady: " + earthQuakesArrayList.size());


        }

    }

}
