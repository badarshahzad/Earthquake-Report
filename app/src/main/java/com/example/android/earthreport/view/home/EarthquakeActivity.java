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
package com.example.android.earthreport.view.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.earthreport.R;
import com.example.android.earthreport.view.setting.SettingFragment;
import com.example.android.earthreport.view.timeline.TimelineFragment;
import com.example.android.earthreport.view.adapters.ViewPagerAdapter;

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

    private ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;


    //TODO: Check this method working or not yet I don't have usb cable
    public void fragmentTransactionCommit(Fragment fragment, String TAG) {

        Fragment existTag = getSupportFragmentManager().findFragmentByTag(TAG);
        Log.i(EarthquakeActivity.TAG, "fragmentTransactionCommit: " + existTag);

        //when menu click the fragment again place into the containter fragment so
        // just one time place if user click same menu again and again (Setting click multiple
        // time but just once placed)
        if (existTag != null) {
            return;
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            // fragmentTransaction.replace(R.id.containerForFragments, fragment, TAG);
            fragmentTransaction.commit();
        }

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        timelineFragment = new TimelineFragment();
        settingFragment = new SettingFragment();
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(timelineFragment);
        viewPagerAdapter.addFragment(settingFragment);
        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //get the root referance
        root = findViewById(R.id.root);

        //The purpose of using viewpager is to mantain the instance of fragment
        // I figure it our the viewpage don't throw away the instance whent the
        // view change as it mantain it in memory so I don't have to add extra code for
        // fragment mantain on each time menu click and load the fragment ag&ag& (again and again)
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        final BottomNavigationView bottomNavg = findViewById(R.id.navBottom);
        bottomNavg.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //CheckedTODO:whenever the menu item click the fragment replace again and
                    // life cycle its not good try to check befor going to replace the fragment
                    //CheckedTODO:Add viewpager to navigate with swap
                    case R.id.home:
                        setTitle("Home");
                        viewPager.setCurrentItem(0);
//                    homeFragment = new HomeFragment();
//                    fragmentTransactionCommit(homeFragment, HOME_FRAGMENT);
                        return true;

                    case R.id.timeline:
                        setTitle("Earthquake List");
                        viewPager.setCurrentItem(1);
//                    timelineFragment = new TimelineFragment();
//                    fragmentTransactionCommit(timelineFragment, TIMELINE_FRAGMENT);
                        return true;

                    case R.id.setting:
                        setTitle("Setting");
                        viewPager.setCurrentItem(2);
//                    settingFragment = new SettingFragment();
//                    fragmentTransactionCommit(settingFragment, SETTING_FRAGMENT);
                        return true;
                }
                return false;
            }

        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                Log.i(TAG, "onPageSelected: "+position+"\n B id: "+bottomNavg.getSelectedItemId()
//                        + " \nId1 " +bottomNavg.getId()+" \nid2: "+bottomNavg.getChildCount()
//                        + " \nid3 " + bottomNavg.getMenu().getItem(position)
//                        + " \nid4 "+ bottomNavg.getMenu());
                bottomNavg.getMenu().getItem(position).setChecked(true);
                setTitle(String.valueOf(bottomNavg.getMenu().getItem(position)));


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //Go and fetch Today, Yesterday, Week, and Month Earthquakes Now
        //  String month = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson";
        //   String week = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
        //  String day = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

        // String []dataFetchURLS = {day,day,week,month};
        //    String []dataFetchURLS = {day};
        //    gogogo(dataFetchURLS);

        //   DataProviderFormat dataProvider = new DataProviderFormat();
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

//                    DownloadData getEarthquakeData = new DownloadData(DataProviderFormat.arrayLists.get(a));
                    DownloadData getEarthquakeData = new DownloadData();

                    getEarthquakeData.execute(URLS);
                }

            }
        };

        Thread th = new Thread(runnable);
        th.start();
    }*/


}
