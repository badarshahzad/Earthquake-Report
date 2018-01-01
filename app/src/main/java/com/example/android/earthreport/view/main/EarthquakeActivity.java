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


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.sync.EarthquakeReminderFirebaseJobService;
import com.example.android.earthreport.model.sync.ReminderUtilities;
import com.example.android.earthreport.view.adapters.ViewPagerAdapter;
import com.example.android.earthreport.view.home.HomeFragment;
import com.example.android.earthreport.view.setting.SettingFragment;
import com.example.android.earthreport.view.timeline.TimelineFragment;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

//what is context This is an abstract class whose implementation is provided by
// the Android system. It allows access to application-specific resources and
//  classes, as well as up-calls for application-level
// operations such as launching activities, broadcasting and receiving intents

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    public static ConstraintLayout root;

    private HomeFragment homeFragment;
    private TimelineFragment timelineFragment;
    private SettingFragment settingFragment;

    private ViewPager viewPager;

    public static Job updateJob(FirebaseJobDispatcher dispatcher) {
        Job newJob = dispatcher.newJobBuilder()
                //update if any task with the given tag exists.
                .setReplaceCurrent(true)
                //Integrate the job you want to start.
                .setService(EarthquakeReminderFirebaseJobService.class)
                .setTag("UniqueTagForYourJob")
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(5, 10))
                .build();
        return newJob;
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
        Job job = createJob(dispatcher);
        dispatcher.mustSchedule(job);
        ReminderUtilities.scheduleEarthquakeReminder(this);
    }

    private Job createJob(FirebaseJobDispatcher dispatcher) {

        Job job = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(EarthquakeReminderFirebaseJobService.class)
                //unique id of the task
                .setTag("UniqueTagForYourJob")
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(5, 10))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .build();
        return job;
    }


}
