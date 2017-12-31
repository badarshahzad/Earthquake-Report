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
package com.example.android.earthreport.view.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.earthreport.R;
import com.example.android.earthreport.view.adapters.ViewPagerAdapter;
import com.example.android.earthreport.view.home.HomeFragment;
import com.example.android.earthreport.view.setting.SettingFragment;
import com.example.android.earthreport.view.timeline.TimelineFragment;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    public static ConstraintLayout root;

    private HomeFragment homeFragment;
    private TimelineFragment timelineFragment;
    private SettingFragment settingFragment;

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

    }

}
