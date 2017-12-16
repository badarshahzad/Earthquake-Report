package com.example.android.earthreport.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.earthreport.EarthQuakeAdapter;
import com.example.android.earthreport.FilterDialog;
import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.main.EarthquakeActivity;
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

    private static final String MIN_MAGNITUDE = "min_magnitude";
    private static final String MAX_MAGNITUDE = "max_magnitude";
    private static final String PERIOD = "period";

    public static List<EarthQuakes> earthQuakesArrayList;
    private final String TAG = TimelineFragment.class.getSimpleName();
    public FilterDialog filterDialog;
    private String todayURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private View view;

    //private View emptyView;
    private TextView emptyText;
    private ImageView noInternetImg;
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

        //TODO:Add like a material design view
        emptyText = view.findViewById(R.id.empty);
        noInternetImg = view.findViewById(R.id.noInternet);

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

        //TODO: Sharedpreferances to store he filter
        //SharefPreferences
       /* SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String  minMag = sharedPref.getString(MIN_MAGNITUDE,"1");
        String  maxMag = sharedPref.getString(MAX_MAGNITUDE,"5");
        String  period = sharedPref.getString(PERIOD,"Today");
*/

        return new EarthquakeLoader(getContext(), todayURL);
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuakes>> loader, List<EarthQuakes> data) {

        Log.d(TAG, "onLoadFinished: ");

        earthListAdapter.clear();
        earthListAdapter.addAll(data);

        //hdie the progressbar
        progressBar.setVisibility(View.GONE);

        /**
         * Title: Check the data connection available or not
         * Author: Sarmad
         * Date: Nov 22 '17
         * Code version: N/A
         * Availability: http://lms.namal.edu.pk/course/view.php?id=267
         */

        // Checked the Network State
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkState = connectivityManager.getActiveNetworkInfo();

        //Test if wifi or data connection available or not
        if (networkState == null || !networkState.isConnected()) {
            //Wifi turn off/on
            final WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                Log.i("WIFI", "ON");
                Snackbar.make(EarthquakeActivity.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Turn on Wifi", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                wifiManager.setWifiEnabled(true);
                                Toast.makeText(getContext(), "WiFi Enabled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        }


        emptyText.setVisibility(View.VISIBLE);
        noInternetImg.setVisibility(View.VISIBLE);

        earthquakeListView.setEmptyView(emptyText);
        earthquakeListView.setEmptyView(noInternetImg);

    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuakes>> loader) {

        earthListAdapter.clear();
        Log.d(TAG, "onLoaderReset: ");
        earthListAdapter.setEarthQuakesList(new ArrayList<EarthQuakes>());

    }


}

