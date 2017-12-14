/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.earthreport.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.earthreport.R;
import com.example.android.earthreport.fragments.HomeFragment;
import com.example.android.earthreport.fragments.SettingFragment;
import com.example.android.earthreport.fragments.TimelineFragment;
import com.example.android.earthreport.network.DownloadData;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    private static final int MENU_ITEM_ABOUT = 1000;
    private static final int REFRESH = 0;
    public static ConstraintLayout root;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    setTitle("Home");
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.containerForFragments, homeFragment, "Home text");
                    fragmentTransaction1.commit();
//                    getSupportActionBar().show();
                    return true;

                case R.id.timeline:
                    setTitle("Earthquake List");
                    TimelineFragment timelineFragment = new TimelineFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.containerForFragments, timelineFragment, "Timeline Text");
                    fragmentTransaction2.commit();
//                    getSupportActionBar().show();
                    return true;

                case R.id.setting:
                    setTitle("Setting");
                    SettingFragment settingFragment = new SettingFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.containerForFragments, settingFragment, "FragmentOne Text");
                    fragmentTransaction3.commit();
//                    getSupportActionBar().hide();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //get the root referance
        root = findViewById(R.id.root);

        BottomNavigationView navigationView = findViewById(R.id.navBottom);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //when the app starts fragment three will be displayed
        setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        //fragmenttransaction is the api for performing a set of fragment
        // operatins such as add, remove,  replace, attach ,detach, hide , and show
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Optional tag name at then end for the fragment to retrive when necessery to find fragment
        fragmentTransaction.replace(R.id.containerForFragments, homeFragment, "Home text");
        fragmentTransaction.commit();


        //Go and fetch Today, Yesterday, Week, and Month Earthquakes Now
        //  String month = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson";
        //   String week = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
        //  String day = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

        // String []dataFetchURLS = {day,day,week,month};
        //    String []dataFetchURLS = {day};
        //    gogogo(dataFetchURLS);

        //   DataProvider dataProvider = new DataProvider();
        //Show the size of list data was enter
        //  Toast.makeText(EarthquakeActivity.this,"Home: "+dataProvider.arrayLists.get(0).size(),Toast.LENGTH_LONG).show();
        //  Toast.makeText(EarthquakeActivity.this,"Home: "+dataProvider.arrayLists.get(1).size(),Toast.LENGTH_LONG).show();
        //  Toast.makeText(EarthquakeActivity.this,"Home: "+dataProvider.arrayLists.get(2).size(),Toast.LENGTH_LONG).show();
        //  Toast.makeText(EarthquakeActivity.this,"Home: "+dataProvider.arrayLists.get(3).size(),Toast.LENGTH_LONG).show();


    }

    private void gogogo(final String[] URLS) {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                // Get data from web
                for (int a = 0; a < URLS.length; a++) {

//                    DownloadData getEarthquakeData = new DownloadData(DataProvider.arrayLists.get(a));
                    DownloadData getEarthquakeData = new DownloadData();

                    getEarthquakeData.execute(URLS);
                }

            }
        };

        Thread th = new Thread(runnable);
        th.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Stop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
    }

}
