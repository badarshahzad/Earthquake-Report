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
package com.badarshahzad.android.earthreport.controller.main;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.badarshahzad.android.earthreport.R;
import com.badarshahzad.android.earthreport.model.utilties.ReminderUtilities;
import com.badarshahzad.android.earthreport.controller.adapters.ViewPagerAdapter;
import com.badarshahzad.android.earthreport.controller.home.HomeFragment;
import com.badarshahzad.android.earthreport.controller.setting.SettingFragment;
import com.badarshahzad.android.earthreport.controller.timeline.TimelineFragment;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**what is context This is an abstract class whose implementation is provided by the
 *  Android system. It allows access to application-specific resources and classes,
 *  as well as up-calls for application-level operations such as launching activities,
 *  broadcasting and receiving intents*/

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    public static ConstraintLayout root;

    private HomeFragment homeFragment;
    private TimelineFragment timelineFragment;
    private SettingFragment settingFragment;

    private AdView adView;

    private ViewPager viewPager;

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
        setContentView(R.layout.main_earthquake_activity);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713

        MobileAds.initialize(this, "ca-app-pub-4583713636574908~2197491969");//id

        adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //get the root referance
        root = findViewById(R.id.root);

        /**The purpose of using viewpager is to mantain the instance of fragment
         * I figure it the viewpage don't throw away the instance when the
         * view change as it mantain it in memory so I don't have to add extra code for
         * fragment mantain on each time menu click and load the fragment ag&ag& (again and again)*/

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        setTitle("Home");

        final BottomNavigationView bottomNavg = findViewById(R.id.navBottom);
        bottomNavg.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //CheckedTODO:whenever the menu item click the fragment replace again and
                    // life cycle its not good try to check before going to replace the fragment
                    //CheckedTODO:Add viewpager to navigate with swap
                    case R.id.home:
                        setTitle("Home");
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.timeline:
                        setTitle("Earthquake List");
                        viewPager.setCurrentItem(1);
                        return true;

                    case R.id.setting:
                        setTitle("Setting");
                        viewPager.setCurrentItem(2);
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
                bottomNavg.getMenu().getItem(position).setChecked(true);
                setTitle(String.valueOf(bottomNavg.getMenu().getItem(position)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        scheduleJob(this);

    }

    private void scheduleJob(Context context) {

        Log.i(TAG, "scheduleJob: ");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        //creating new job and adding it with dispatcher
        ReminderUtilities.scheduleEarthquakeReminder(this);
    }




}
