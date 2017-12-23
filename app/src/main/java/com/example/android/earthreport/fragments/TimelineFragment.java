package com.example.android.earthreport.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.earthreport.EarthQuakeAdapter;
import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.main.EarthquakeActivity;
import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.EarthquakeLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<EarthQuakes>> {


    public static final String KEY_FILTER_MAGNITUDE = "key_filter_magnitude";
    public static final String KEY_PERIOD = "key_period";
    public static final String KEY_SORT = "key_sort";
    public static final String MARK_TYPE = "MARK_TYPE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String CITY = "CITY";
    public static final String MAGNITUDE = "MAGNITUDE";
    public static final String DATE = "DATE";
    public static final String DATA = "DATA";
    public static final int NO_DATA_ERROR = R.drawable.ic_error_colored_24dp;
    public static final int NO_INTERNET_ERROR = R.drawable.ic_cloud_off_black_24dp;
    public static final String NO_DATA_TEXT = "No data retrieve check Filter or Period in Setting.";
    public static final String NO_INTERNET_TEXT = "No Internet Connection";

    private final String TAG = TimelineFragment.class.getSimpleName();
    private List<EarthQuakes> earthQuakesArrayList;

    //Period_URL
    private String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    private String HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    private String TODAY_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";


    private View view;
    private TextView emptyStateText;
    private ImageView emptyStateImagView;

    private ListView earthquakeListView;
    private FloatingActionButton fabButton;
    private EarthQuakeAdapter earthListAdapter;

    private SwipeRefreshLayout swipeRefresh;


    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Yes it has the menu item
        setHasOptionsMenu(true);

        //Fragment protect from destroy and recreate and retain
        // the current instance of the fragment when the activity is recreated.
        // CheckedTodo: The retaininstance helping me on activity orientation change
        setRetainInstance(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Firstly clear the existed menu & and add my own menu
        menu.clear();
        inflater.inflate(R.menu.menu_home, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        //Referesh will update the earthquakes count and hourly earthquakes list
        if (itemId == R.id.action_refresh) {
            loadData();
            swipeRefresh.setRefreshing(true);

        }

        return super.onOptionsItemSelected(item);
    }


    //Always add adapter first! Always add adapter first! Always add adapter first!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //CheckedkTODO:Set the timline view

        //test the one list only
        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        emptyStateText = view.findViewById(R.id.empty);
        emptyStateImagView = view.findViewById(R.id.noInternet);

        //TODO: Add swipe listener
        earthquakeListView = view.findViewById(R.id.list);

        //CheckedTODO:Add like a material design view
        fabButton = view.findViewById(R.id.fabButton);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh: ");
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                loadData();
                            }
                        });

                        invisibleState();
                        earthListAdapter.clear();
                    }
                }
        );

        earthQuakesArrayList = new ArrayList<>();
        earthListAdapter = new EarthQuakeAdapter(getContext(), R.layout.earthquake_item, earthQuakesArrayList);
        earthquakeListView.setAdapter(earthListAdapter);

        //I can start the load data from here with just forceLoad at the end but
        //I added in onStartLoading method of AyscnLoader class the correct way
        //getLoaderManager().initLoader(1, null, this).forceLoad();
        //Log.d(TAG, "getLoaderManger init: ");

        loadData();
        //Log.d(TAG, "getLoaderManger created: ");

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), Map.class);

                Bundle bundle = new Bundle();
                bundle.putString(MARK_TYPE, "single");
                bundle.putDouble(LONGITUDE, earthQuakesArrayList.get(position).getLongitude());
                bundle.putDouble(LATITUDE, earthQuakesArrayList.get(position).getLatitude());
                bundle.putString(CITY, earthQuakesArrayList.get(position).getCityname());
                bundle.putString(MAGNITUDE, earthQuakesArrayList.get(position).getMagnitude());
                bundle.putString(DATE, earthQuakesArrayList.get(position).getDate());


                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Title:
                 * Author: Javier Armero Ciudad, A. Raza
                 * Date: 2017/12/18
                 * Code version: N/A
                 * Availability: https://stackoverflow.com/questions/13601883/how-to-pass-arraylist-of-objects-from-one-to-another-activity-using-intent-in-an/13616719
                 */

                // From above mention source I learn how to send arraylist of objects in bundle

                Intent intent = new Intent(getContext(), Map.class);
                Bundle bundle = new Bundle();
                bundle.putString(MARK_TYPE, "multiple");
                bundle.putSerializable(DATA, (Serializable) earthQuakesArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadData() {

//        Log.i(TAG, "loadData: ");
        //Http requet to fetch jason data at that time
        //getLoaderManager().initLoader(0, null, this);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(1, null, this);
//        Log.i(TAG, "loadData: init called");
    }


    //Return a new loader instance that is ready to start loading
    @Override
    public Loader<List<EarthQuakes>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: ");

        //CheckedTODO: Sharedpreferances to store the filter
        //SharefPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        String filterMag = sharedPref.getString(KEY_FILTER_MAGNITUDE, "5");
        String period = sharedPref.getString(KEY_PERIOD, "Today");
        String orderBy = sharedPref.getString(KEY_SORT, "Time");

//        Log.i(TAG, "filterMag: " + filterMag);
//        Log.i(TAG, "period: " + period);
//        Log.i(TAG, "orderBy: " + orderBy.toLowerCase());


        switch (period) {
            case "1 Hour":
                URL = HOUR_URL;
//                Log.i(TAG, "1 hour");
                break;

            case "1 Day":
                URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getTodayDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, "1 day ");
                break;

            case "Yesterday":
                URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getYesterdayDate() + "T00:00&endtime=" + getYesterdayDate() + "T23:59";
//                Log.i(TAG, "yesterday ");
                break;

            case "Week":
                URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getWeekDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, " week");
                break;

            case "Month":
                URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getMonthDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, " Month");
                break;

            default:
                URL = TODAY_URL;
        }

        int length = URL.length();

        if (filterMag.equals("All")) {
            filterMag = "0";
        }

        //Make the url according to to the settings of user
        Uri uri = Uri.parse(URL);
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("format", "geojson");
        builder.appendQueryParameter("minmagnitude", filterMag);
        builder.appendQueryParameter("orderby", orderBy.toLowerCase());

        URL = builder.toString();

        //URL.replace('&','?');
        //Url with ? mark not get the result from web
        StringBuilder sb = new StringBuilder(URL);
        sb.setCharAt(length, '&');
        URL = sb.toString();
        Log.i(TAG, "url ready: " + URL);


        Log.i(TAG, "onCreateLoader: going to finish");
        //String url1 = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson&format=geojson&orderby=magnitude-asc";
        return new EarthquakeLoader(getContext(), URL);
    }

    //Called when the previously created loader has finished its load
    //This work on a main thread
    @Override
    public void onLoadFinished(Loader<List<EarthQuakes>> loader, final List<EarthQuakes> data) {

        //CheckdTODO: Handle the empty list

        Log.d(TAG, "onLoadFinished: ");

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

                //set the text and image when no internet
                showState(NO_INTERNET_TEXT, NO_INTERNET_ERROR);
                visibleState();

//                Log.i(TAG, "OFF");
                Snackbar.make(EarthquakeActivity.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Turn on Wifi", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                wifiManager.setWifiEnabled(true);
                                Toast.makeText(getContext(), "WiFi Enabled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return;
            }
        }

        if (data.size() == 0) {

//         Log.i(TAG, "onLoadFinished:" + earthListAdapter.getCount());*/
            earthListAdapter.clear();
            earthListAdapter.addAll(data);
            showState(NO_DATA_TEXT, NO_DATA_ERROR);
            visibleState();

            return;
        }


        emptyStateText.setVisibility(View.INVISIBLE);
        emptyStateImagView.setVisibility(View.INVISIBLE);

        swipeRefresh.setRefreshing(false);
//         Log.i(TAG, "onLoadFinished:" + earthListAdapter.getCount());
        earthListAdapter.clear();
//         Log.i(TAG, "onLoadFinished:" + earthListAdapter.getCount());
        earthListAdapter.addAll(data);
//         Log.i(TAG, "onLoadFinished:" + earthListAdapter.getCount());

    }

    //Invisible the text and Icon
    private void invisibleState() {
        emptyStateText.setVisibility(View.INVISIBLE);
        emptyStateImagView.setVisibility(View.INVISIBLE);
    }

    //Visible the text and Icon
    private void visibleState() {
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateImagView.setVisibility(View.VISIBLE);
    }

    private void showState(String text, int img_src) {

        emptyStateText.setText(text);
        emptyStateImagView.setImageResource(img_src);
        //Hide the refresh icon
        swipeRefresh.setRefreshing(false);
    }

    //This is called when the last data provided to onLoadFinished()
    // above is about to be closed.  We need to make sure we are no
    // longer using it.
    @Override
    public void onLoaderReset(Loader<List<EarthQuakes>> loader) {

//        Log.d(TAG, "onLoaderReset: ");

        //CheckdTODO: Check when the onLoaderReset call and clear adapter and set the new arraylist
        //CheckdTODO: Find this solution in loader after much effor but Honestly figure out what the problem facing how why I did this

        //Remove all elementes from arrayadapter
        earthListAdapter.clear();
        swipeRefresh.setRefreshing(false);
        // earthListAdapter.setEarthQuakesList(new ArrayList<EarthQuakes>());

    }

    public String getYesterdayDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdayDate = calendar.getTime();
//        Log.i(TAG, "Yesterday Date Ready: " + yesterdayDate);

        return DataProvider.getformateDate(yesterdayDate);
    }

    public String getTodayDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdayDate = calendar.getTime();
        Log.i(TAG, "Yesterday Date Ready: " + yesterdayDate);

        return DataProvider.getformateDate(yesterdayDate);
    }

    public String getWeekDate() {

        // Made the date 7 days back
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDateForWeek = calendar.getTime();
        Log.i(TAG, "Seven day back " + newDateForWeek);

        return DataProvider.getformateDate(newDateForWeek);

    }

    public String getMonthDate() {

        //Made the 1st date of this month
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date newDateForMonth = calendar1.getTime();
        Log.i(TAG, "Month date " + newDateForMonth);

        return DataProvider.getformateDate(newDateForMonth);
    }

    // The below override method may be have bug as its not work
    // when I implement the SwipeListenerLayout and give implementation in this method
    /*@Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }*/
}

