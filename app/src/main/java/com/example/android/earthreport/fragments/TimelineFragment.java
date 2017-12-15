package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.earthreport.FilterDialog;
import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.GetEarthquakeData;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {


    private static final int MENU_ITEM_ABOUT = 1000;
    ArrayList<EarthQuakes> earthQuakesList;

    private View view;

    private String todayURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private ProgressBar progressBar;
    private ListView earthquakeListView;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Firstly clear the existed menu & and add my own menu
        menu.clear();
        // menu.add(0, MENU_ITEM_ABOUT, 102, R.string.about);
        inflater.inflate(R.menu.menu_timline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(getContext(), "Timline refresh", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_filter) {
            //Toast.makeText(getContext(), "Timline Filter", Toast.LENGTH_SHORT).show();

            //-------Show dialog-----------
            FragmentManager fm = getFragmentManager();
            FilterDialog filterDialog = new FilterDialog();
            filterDialog.show(fm, "");

            //-------------Show Activity---
            //  Intent intent = new Intent(getContext(), ShowEarthquakeDetails.class);
            //   startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    //Always add adapter first! Always add adapter first! Always add adapter first!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO:Set the timline view


        //test the one list only
        view = inflater.inflate(R.layout.fragment_datalist, container, false);

        earthquakeListView = view.findViewById(R.id.list);

        progressBar = view.findViewById(R.id.progress_bar);

        earthQuakesList = new ArrayList<>();

        // Get data from web of this hour earthquakes
        GetEarthquakeData getEarthquakeData = new GetEarthquakeData(getContext(), earthquakeListView, earthQuakesList);
        getEarthquakeData.execute(todayURL);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), Map.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("LONGITUDE", earthQuakesList.get(position).getLongitude());
                bundle.putDouble("LATITUDE", earthQuakesList.get(position).getLatitude());
                bundle.putString("CITY", earthQuakesList.get(position).getCityname());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        return view;
    }


    public void displayProgressBar(boolean display) {
        if (display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


}
