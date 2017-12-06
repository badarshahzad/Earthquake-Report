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
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.earthreport.FragmentOne;
import com.example.android.earthreport.R;
import com.example.android.earthreport.fragments.HomeFragment;
import com.example.android.earthreport.fragments.TimelineFragment;

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
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.containerForFragments, homeFragment, "Home text");
                    fragmentTransaction3.commit();
//                    getSupportActionBar().show();
                    return true;

                case R.id.timeline:
                    setTitle("Timeline");
                    TimelineFragment timelineFragment = new TimelineFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.containerForFragments, timelineFragment, "Timeline Text");
                    fragmentTransaction2.commit();
//                    getSupportActionBar().show();
                    return true;

                case R.id.setting:
                    setTitle("Setting");
                    FragmentOne fragment1 = new FragmentOne();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.containerForFragments, fragment1, "FragmentOne Text");
                    fragmentTransaction1.commit();
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
        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        //Optional tag name at then end for the fragment to retrive when necessery to find fragment
        fragmentTransaction3.replace(R.id.containerForFragments, homeFragment, "Home text");
        fragmentTransaction3.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //About option
        menu.add(0, MENU_ITEM_ABOUT, 102, R.string.about);

        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_ABOUT:
                Snackbar.make(root, "You selected about.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
//                Toast.makeText(context,"You select about",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:

        }
        return super.onOptionsItemSelected(item);
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
