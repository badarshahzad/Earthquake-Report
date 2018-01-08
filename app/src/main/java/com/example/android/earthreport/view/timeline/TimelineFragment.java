package com.example.android.earthreport.view.timeline;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.api.EarthquakeLoader;
import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.model.utilties.DataProviderFormat;
import com.example.android.earthreport.view.adapters.EarthQuakeAdapter;
import com.example.android.earthreport.view.main.EarthquakeActivity;
import com.example.android.earthreport.view.map.Map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<EarthQuakes>>, DatePickerDialog.OnDateSetListener {


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
    public static final String SINGLE = "single";
    public static final String MULTIPLE = "multiple";
    public static final String SEARCH_OR_PERIOD = "SEARCH_OR_PERIOD";
    public static final String SET_URL_SEARCH = "Search";
    public static final String SET_URL_PERIOD = "Period";

    private final String TAG = TimelineFragment.class.getSimpleName();
    public String selectedStartDate;
    public String selectedEndDate;
    private List<EarthQuakes> earthQuakesArrayList;

    //Period_URL
    private String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    private String HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    private String TODAY_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

    private String selectedMinMagnitude;
    private String selectedOrderBy;


    private View view;
    private TextView emptyStateText;
    private ImageView emptyStateImagView;
    private Spinner counterySpinner;
    private Spinner minMagnitudeSpinner;
    private Spinner orderBySpinner;
    private Button startDate;
    private Button endDate;
    private DatePickerDialog datePickerDialog;
    private boolean startEndDateGuard = true;
    private boolean searchOrPeriodGuard = true;
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
        inflater.inflate(R.menu.menu_timline, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        //Referesh will update the earthquakes count and hourly earthquakes list
        if (itemId == R.id.action_refresh) {
            loadData();
            //swipeRefresh.setRefreshing(true);
        }

        //TODO:Add the action filter in the timeline also to get user quick access to the filter
        if (itemId == R.id.action_search){

            showSearchDialog();
            //showFilterDialog();

        }

        return super.onOptionsItemSelected(item);
    }
/*

    private void showFilterDialog() {

//            FilterDialog filterDialog = new FilterDialog();
//            filterDialog.show(getFragmentManager(),"action_filter");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_filter, null);

        minMagnitudeSpinner = view.findViewById(R.id.minOfSpinner);
        counterySpinner = view.findViewById(R.id.countryOfSpinner);

        List<String> countries = HomeFragment.getCountriesName();
        //sort the countries name to show in spinner a-z
        Collections.sort(countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                countries
        );

        counterySpinner.setAdapter(adapter);

        AlertDialog.Builder addAlert = new AlertDialog.Builder(getContext());
        addAlert.setTitle("Filter Results");
        addAlert.setView(view);
        addAlert.setCancelable(false);
        addAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
            }
        });

        addAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //TODO: filter the data exist in the listview
                selectedMinMagnitude = String.valueOf(minMagnitudeSpinner.getSelectedItem());
                selectedCountry = String.valueOf(counterySpinner.getSelectedItem());


//                    Toast.makeText(getContext(),"Done",Toast.LENGTH_SHORT).show();

            }
        });

        AlertDialog dialog = addAlert.create();
        dialog.show();
    }
*/


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

        //I can startEndDateGuard the load data from here with just forceLoad at the end but
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
                bundle.putString(MARK_TYPE, SINGLE);
                bundle.putDouble(LONGITUDE, earthQuakesArrayList.get(position).getLongitude());
                bundle.putDouble(LATITUDE, earthQuakesArrayList.get(position).getLatitude());
                bundle.putString(CITY, earthQuakesArrayList.get(position).getCityname());
                bundle.putString(MAGNITUDE, earthQuakesArrayList.get(position).getMagnitude());
                bundle.putString(DATE, earthQuakesArrayList.get(position).getTimeStamp());


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
                bundle.putString(MARK_TYPE, MULTIPLE);
                bundle.putSerializable(DATA, (Serializable) earthQuakesArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view;
    }

    private void loadData() {

//        Log.i(TAG, "loadData: ");

        //. If the loader doesn't already exist, one is created and
        // (if the activity/fragment is currently started) starts the loader.
        // Otherwise the last created loader is re-used.
        LoaderManager loaderManager = getLoaderManager();

        if(loaderManager.hasRunningLoaders()){

            Toast.makeText(getContext(),"Yes loader Running",Toast.LENGTH_SHORT).show();

        }else {

            Bundle bundle = new Bundle();

            if(searchOrPeriodGuard) {

                bundle.putString(SEARCH_OR_PERIOD,SET_URL_PERIOD);
            }else{
                bundle.putString(SEARCH_OR_PERIOD,SET_URL_SEARCH);

            }
            loaderManager.restartLoader(1,bundle , this);

            //getSupportLoaderManager().initLoader(1, null, this);
        }
    }

    //I need this in Home to access instantly to get the formated url so that's why static
    public static String getDateFormatedURL(String startDate, String endDate){

        //This is necessery to mention the time from 00:00 to 23:59 as we have time difference country to country
        //Say thanks to USGS so such a facility
        return "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + startDate + "T00:00&endtime=" + endDate + "T23:59";
    }

    //Return a new loader instance that is ready to startEndDateGuard loading
    @Override
    public Loader<List<EarthQuakes>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: ");

        //CheckedTODO: Sharedpreferances to store the filter
        //Default sharedpreferances share the same document
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String filterMag = sharedPref.getString(KEY_FILTER_MAGNITUDE, "5");
        String orderBy = sharedPref.getString(KEY_SORT, "Time");
        String period = null;

        //check period if user tap for search then do search otherwise normal period setting use
        if(args.getString(SEARCH_OR_PERIOD).equals(SET_URL_PERIOD)){

            //by default period is today in case user don't select
            period = sharedPref.getString(KEY_PERIOD, "Today");
            //this guard for swipe down refresh recall what does user tap last time
            //true for period
            searchOrPeriodGuard = true;

        }else{

            //set the search url if user tap search menu item
            period = SET_URL_SEARCH;
            //false for search
            searchOrPeriodGuard = false;
        }
//        Log.i(TAG, "filterMag: " + filterMag);
//        Log.i(TAG, "period: " + period);
//        Log.i(TAG, "orderBy: " + orderBy.toLowerCase());


        switch (period) {
            case "1 Hour":
                URL = HOUR_URL;
//                Log.i(TAG, "1 hour");
                break;

            case "1 Day":
                URL = getDateFormatedURL(getTodayDate(),getTodayDate());//"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getTodayDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, "1 day ");
                break;

            case "Yesterday":
                URL = getDateFormatedURL(getYesterdayDate(),getYesterdayDate());//"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getYesterdayDate() + "T00:00&endtime=" + getYesterdayDate() + "T23:59";
//                Log.i(TAG, "yesterday ");
                break;

            case "Week":
                URL = getDateFormatedURL(getWeekDate(),getTodayDate());//"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getWeekDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, " week");
                break;

            case "Month":
                //The month date is every month 1st date of month
                URL = getDateFormatedURL(getMonthDate(),getTodayDate());//"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + getMonthDate() + "T00:00&endtime=" + getTodayDate() + "T23:59";
//                Log.i(TAG, " Month");
                break;

            case "Search":
                //search url (sorry for hard coded entitle me another way if you know send pull request)
                URL = getDateFormatedURL(selectedStartDate,selectedEndDate);
                Log.i(TAG, "search: "+selectedStartDate+" : "+selectedEndDate);
                Log.i(TAG, "search: url: "+URL);
                filterMag = selectedMinMagnitude;
                orderBy = selectedOrderBy;
                break;

            default:
                URL = TODAY_URL;
        }

        /**
         * Well this length necessary kind of hardcoded again as the custom query with custom method
         * set in url i have to put '&' instead of '?' (tell me if you know better way)
         */
        int length = URL.length();

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
                visibleState(NO_INTERNET_TEXT, NO_INTERNET_ERROR);

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
        }else if(data.size() == 0) {

//         Log.i(TAG, "onLoadFinished:" + earthListAdapter.getCount());*/
            earthListAdapter.clear();
            earthListAdapter.addAll(data);

            visibleState(NO_DATA_TEXT, NO_DATA_ERROR);

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        //TODO: incrementing the month by 1 as it contains 0 starting from 0 index
        month++;
        if (startEndDateGuard) {
            selectedStartDate = year + "-" + month + "-" + dayOfMonth;
            Log.i(TAG, "onCreate: " + selectedStartDate);
        } else {
            selectedEndDate = year + "-" + month + "-" + dayOfMonth;
            Log.i(TAG, "onClick: " + selectedEndDate);
        }
    }
    //Invisible the text and Icon
    private void invisibleState() {
        emptyStateText.setVisibility(View.INVISIBLE);
        emptyStateImagView.setVisibility(View.INVISIBLE);
    }

    //Visible the text and Icon
    private void visibleState(String text, int img_src) {

        emptyStateText.setText(text);
        emptyStateImagView.setImageResource(img_src);
        //Hide the refresh icon
        swipeRefresh.setRefreshing(false);

        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateImagView.setVisibility(View.VISIBLE);
    }

    //search Dialog Still not design as I want
    public void showSearchDialog() {


        //TODO:Add the action filter in the timeline also to get user quick access to the filter
        //to retrieve a standard LayoutInflater instance that is already hooked up to the current
        // context and correctly configured for the device you are running on.
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_search_filter, null);

        minMagnitudeSpinner = view.findViewById(R.id.minOfSpinner);
        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);
        orderBySpinner = view.findViewById(R.id.orderBy);

        //this will open the date picker dialoge to pic the startEndDateGuard date
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //this startEndDateGuard will ensure that in dateSet method to set value in selectedStartdate
                startEndDateGuard = true;
                //Datepicker will show
                showDatePicker();
            }
        });

        //this will open the date picker dialoge to pic the end date
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //this startEndDateGuard will ensure that in dateSet method to set value in selectedEnddate
                startEndDateGuard = false;
                //Datepicker will show
                showDatePicker();
                //selectedEndDate = DatePickerFragment.getTimeStamp();

            }
        });

        //Designing the alert box
        AlertDialog.Builder addAlert = new AlertDialog.Builder(getContext());
        addAlert.setTitle("Filter Search");
        addAlert.setView(view);
        addAlert.setCancelable(false);
        addAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
            }
        });

        addAlert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                selectedMinMagnitude = String.valueOf(minMagnitudeSpinner.getSelectedItem());
                selectedOrderBy = String.valueOf(orderBySpinner.getSelectedItem());
//                Toast.makeText(SearchEarthquakeActivity.this, latLng + " " + locationName + " " + selectedEndDate + " " + selectedStartDate, Toast.LENGTH_LONG).show();

                //this hide the status of messages from screen
                invisibleState();
                //this will fectch and load data
                loadData();
                //search guard for search url check
                searchOrPeriodGuard = false;
            }
        });

        //AlertDialog design pass and ready to show
        AlertDialog dialog = addAlert.create();
        dialog.show();

    }

    //show datepicker dialoge with current date
    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        //TODO: Why the month is starting from 0
//        Log.i(TAG, "month: "+month);
        int year = c.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
        datePickerDialog.show();
    }

    public String getYesterdayDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdayDate = calendar.getTime();
//        Log.i(TAG, "Yesterday Date Ready: " + yesterdayDate);

        return DataProviderFormat.getformateDate(yesterdayDate);
    }

    public String getTodayDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdayDate = calendar.getTime();
        Log.i(TAG, "Yesterday Date Ready: " + yesterdayDate);

        return DataProviderFormat.getformateDate(yesterdayDate);
    }

    public String getWeekDate() {

        // Made the date 7 days back
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDateForWeek = calendar.getTime();
        Log.i(TAG, "Seven day back " + newDateForWeek);

        return DataProviderFormat.getformateDate(newDateForWeek);

    }

    public String getMonthDate() {

        //Made the 1st date of this month
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date newDateForMonth = calendar1.getTime();
        Log.i(TAG, "Month date " + newDateForMonth);

        return DataProviderFormat.getformateDate(newDateForMonth);
    }

}

