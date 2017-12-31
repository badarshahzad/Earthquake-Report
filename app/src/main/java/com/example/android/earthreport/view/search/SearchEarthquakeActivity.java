package com.example.android.earthreport.view.search;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.earthreport.R;


public class SearchEarthquakeActivity extends AppCompatActivity {


    public static final String TAG = SearchEarthquakeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


}
