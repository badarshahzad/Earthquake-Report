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
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.earthreport.R;
import com.example.android.earthreport.fragments.HomeFragment;
import com.example.android.earthreport.fragments.SettingFragment;
import com.example.android.earthreport.fragments.TimelineFragment;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String HOME_FRAGMENT = "home_fragment";
    public static final String TIMELINE_FRAGMENT = "timeline_fragment";
    public static final String SETTING_FRAGMENT = "setting_fragment";
    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    private static final int MENU_ITEM_ABOUT = 1000;
    private static final int REFRESH = 0;
    public static ConstraintLayout root;

    private HomeFragment homeFragment;
    private TimelineFragment timelineFragment;
    private SettingFragment settingFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                //TODO:whenever the menu item click the fragment replace again and
                // life cycle its not good try to check befor going to replace the fragment
                //TODO:Add viewpager to navigate with swap
                case R.id.home:
                    setTitle("Home");
                    homeFragment = new HomeFragment();
                    fragmentTransactionCommit(homeFragment, HOME_FRAGMENT);
                    return true;

                case R.id.timeline:
                    setTitle("Earthquake List");
                    timelineFragment = new TimelineFragment();
                    fragmentTransactionCommit(timelineFragment, TIMELINE_FRAGMENT);
                    return true;

                case R.id.setting:
                    setTitle("Setting");
                    settingFragment = new SettingFragment();
                    fragmentTransactionCommit(settingFragment, SETTING_FRAGMENT);
                    return true;
            }
            return false;
        }
    };

    //TODO: Check this method working or not yet I don't have usb cable
    public void fragmentTransactionCommit(Fragment fragment, String TAG) {

        Fragment existTag = getSupportFragmentManager().findFragmentByTag(TAG);
        Log.i(EarthquakeActivity.TAG, "fragmentTransactionCommit: " + existTag);

        //when menu click the fragment again place into the containter fragment so
        // just one time place if user click same menu again and again (Setting click multiple
        // time but just once placed)
//        if (existTag!=null) {
//            return;
//        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containerForFragments, fragment, TAG);
            fragmentTransaction.commit();
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //get the root referance
        root = findViewById(R.id.root);

        BottomNavigationView navigationView = findViewById(R.id.navBottom);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //TODO:Does these tags are working or not
        //Find the retain fragment on activity restarts
        FragmentManager fragmentManager = getSupportFragmentManager();
        homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(HOME_FRAGMENT);
        Log.i(TAG, "onCreate: " + homeFragment);
        timelineFragment = (TimelineFragment) fragmentManager.findFragmentByTag(TIMELINE_FRAGMENT);
        settingFragment = (SettingFragment) fragmentManager.findFragmentByTag(SETTING_FRAGMENT);

        //TODO:Check the fragments or retaining or not

        //when the app starts fragment will be null so
        if (homeFragment == null && timelineFragment == null && settingFragment == null) {
            setTitle("Home");
            homeFragment = new HomeFragment();
            //fragmenttransaction is the api for performing a set of fragment
            // operatins such as add, remove,  replace, attach ,detach, hide , and show
            //Optional tag name at then end for the fragment to retrive when necessery to find fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.containerForFragments, homeFragment, "Home text")
                    .commit();
        }

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
/*
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
    }*/

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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

}
