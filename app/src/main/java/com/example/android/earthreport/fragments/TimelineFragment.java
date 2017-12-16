package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.earthreport.EarthQuakeAdapter;
import com.example.android.earthreport.FilterDialog;
import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.EarthquakeLoader;
import com.example.android.earthreport.network.GetEarthquakeData;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<EarthQuakes>> {


    private static final int MENU_ITEM_ABOUT = 1000;
    public static List<EarthQuakes> earthQuakesArrayList;
    private final String TAG = TimelineFragment.class.getSimpleName();
    public FilterDialog filterDialog;
    private String todayURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private View view;
    //private View emptyView;
    private TextView emptyText;
    private ProgressBar progressBar;
    private ListView earthquakeListView;
    private GetEarthquakeData getEarthquakeData;
    private EarthQuakeAdapter earthListAdapter;



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
            filterDialog = new FilterDialog();
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
        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        emptyText = view.findViewById(R.id.empty);

        //TODO:Empty view
        //emptyview
        //emptyView = inflater.inflate(R.layout.fragment_empty, container, false);

        earthquakeListView = view.findViewById(R.id.list);

        progressBar = view.findViewById(R.id.progress_bar);


        earthQuakesArrayList = new ArrayList<>();

        // Get data from web of this hour earthquakes
        // getEarthquakeData = new GetEarthquakeData(getContext(), earthquakeListView);
        // getEarthquakeData.execute(todayURL);


        earthListAdapter = new EarthQuakeAdapter(getContext(), R.layout.earthquake_item, earthQuakesArrayList);
        earthquakeListView.setAdapter(earthListAdapter);

        Log.d(TAG, "getLoaderManger init: ");
        getLoaderManager().initLoader(1, null, this).forceLoad();
        Log.d(TAG, "getLoaderManger created: ");

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), Map.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("LONGITUDE", earthQuakesArrayList.get(position).getLongitude());
                bundle.putDouble("LATITUDE", earthQuakesArrayList.get(position).getLatitude());
                bundle.putString("CITY", earthQuakesArrayList.get(position).getCityname());
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


    public void filterRefreshList(String selectedPeriod, final String selectedMin, final String selectedMax, String selectedregion) {

        Log.i(TAG, "onOptionsItemSelected: " + selectedPeriod);
        Log.i(TAG, "onOptionsItemSelected: " + selectedMin);
        Log.i(TAG, "onOptionsItemSelected: " + selectedMax);
        Log.i(TAG, "onOptionsItemSelected: " + selectedregion);


        Log.i(TAG, "Size: " + earthQuakesArrayList.size());


        ArrayList<EarthQuakes> newEarthQuakesList = new ArrayList<>();

        double var = 0;
        for (int a = 0; a < earthQuakesArrayList.size(); a++) {

            //Minimum
            var = Double.valueOf(earthQuakesArrayList.get(a).getMagnitude());
            if (var >= Double.valueOf(selectedMin) &&
                    var <= Double.valueOf(selectedMax)) {
                newEarthQuakesList.add(earthQuakesArrayList.get(a));

                Log.i(TAG, "run:" + earthQuakesArrayList.get(a).getMagnitude());

            }

        }

        //TODO: Update the list according to the filter
        // earthQuakesArrayList =  newEarthQuakesList;
        //Update view
        //  EarthQuakeAdapter earthListAdapter = new EarthQuakeAdapter(getContext(), R.layout.earthquake_item, earthQuakesArrayList);
        //  earthquakeListView.setAdapter(earthListAdapter);
    }


    @Override
    public Loader<List<EarthQuakes>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        return new EarthquakeLoader(getContext(), todayURL);
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuakes>> loader, List<EarthQuakes> data) {

        Log.d(TAG, "onLoadFinished: ");

        earthListAdapter.clear();
        earthListAdapter.addAll(data);

        emptyText.setVisibility(View.VISIBLE);
        earthquakeListView.setEmptyView(emptyText);

    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuakes>> loader) {

        earthListAdapter.clear();
        Log.d(TAG, "onLoaderReset: ");
        earthListAdapter.setEarthQuakesList(new ArrayList<EarthQuakes>());

    }


}

