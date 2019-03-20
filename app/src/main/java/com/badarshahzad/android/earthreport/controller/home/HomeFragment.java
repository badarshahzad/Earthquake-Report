package com.badarshahzad.android.earthreport.controller.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.badarshahzad.android.earthreport.R;
import com.badarshahzad.android.earthreport.controller.adapters.EarthQuakeAdapter;
import com.badarshahzad.android.earthreport.controller.favourit.FavouriteActivity;
import com.badarshahzad.android.earthreport.controller.main.EarthquakeActivity;
import com.badarshahzad.android.earthreport.controller.map.Map;
import com.badarshahzad.android.earthreport.controller.timeline.TimelineFragment;
import com.badarshahzad.android.earthreport.model.api.EarthquakeLoader;
import com.badarshahzad.android.earthreport.model.pojos.EarthQuakes;
import com.badarshahzad.android.earthreport.model.pojos.FavoriteCountries;
import com.badarshahzad.android.earthreport.model.utilties.DataProviderFormat;
import com.badarshahzad.android.earthreport.model.utilties.ParseFavoritCountriesJsonUtils;
import com.badarshahzad.android.earthreport.model.utilties.ParseUSGSJsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    public static final String FAVOURIT_LIST = "favouritList";
    public static final String TODAY = "Today";
    public static final String YESTERDAY = "Yesterday";
    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    private static final String TAG = HomeFragment.class.getSimpleName();
    public static String TODAY_COUNT_KEY = "todayCountkey";
    public static String YESTERDAY_COUNT_KEY = "yesterdayCountkey";
    public static String WEEK_COUNT_KEY = "weekCountkey";
    public static String MONTH_COUNT_KEY = "monthCountkey";
    public static final String COUNT_KEY_ARRAY[] = {TODAY_COUNT_KEY, YESTERDAY_COUNT_KEY, WEEK_COUNT_KEY, MONTH_COUNT_KEY};
    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static Handler handler;
    public static String todayDate = DataProviderFormat.getformateDate(new Date());
    private static String COUNT_KEY = "countKey";
    public Double selectedMinMagnitude;
    public String selectedCountery;

    public Handler handlerLatest;
    private String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private String HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private TextView todayEarthquakes;
    private TextView yesterdayEarthquakes;
    private TextView thisMonthEarthquakes;
    private TextView thisWeekEarthquakes;
    private TextView todayMapStatus;
    private String[] countURLS = new String[4];
    private ProgressBar progressBar;
    private List<EarthQuakes> alldayList;
    private String selectedSearchPlaceName;
    private LatLng selectedSearchPlaceLatLng;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private SharedPreferences sharedPreferences;

    private List<EarthQuakes> earthQuakesArrayList;

    private List<EarthQuakes> latestEarthquakesList;
    private EarthQuakeAdapter earthListAdapter;

    private ListView latestEarthquakesListView; //in this hour
    //Today earthquakes count
    private String CountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";

    private boolean isScreeVisible = false;
    private boolean isInternetAvailable = false;

    private AdView adView1;
    private AdView adView2;
    private AdView adView3;
    


    public HomeFragment() {

    }

    @Override
    public void onDestroy() {

        if(adView1 !=null){
            adView1.destroy();
        }

        if(adView2 !=null){
            adView2.destroy();
        }

        if(adView3 !=null){
            adView3.destroy();
        }


        super.onDestroy();
    }

    //Get the all countries name
    public static ArrayList<String> getCountriesName() {
        ArrayList<String> list = new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            //Log.i(TAG, "Country Name = " + obj.getDisplayCountry());
            list.add(obj.getDisplayCountry());

        }
        return list;
    }

    //Does the Lati&Longi is equale to someone countery?
    public static boolean isCounteryExist(Context context, double latitude, double longitude) {
        String counteryName = null;
        try {
            counteryName = getCountryName(context, latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            //Log.i(TAG, "Country Name = " + obj.getDisplayCountry());
            list.add(obj.getDisplayCountry());


            if (counteryName != null && counteryName.equals(obj.getDisplayCountry())) {
//                Log.i(TAG, "Get Display Countery:" + obj.getDisplayCountry());
//                Log.i(TAG, "Countery Name: " + obj.getDisplayCountry());
                return true;
            }
        }
        return false;
    }

    //if name exist then tell me the name
    public static String getCountryName(Context context, double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.isEmpty()) {
            return null;
        }
        //Log.i(TAG, "getCountryName:1 "+addresses.get(0).getCountryName());
        Address result;

        if (addresses != null && !addresses.isEmpty()) {
            //  Log.i(TAG, "getCountryName:2 "+addresses.get(0).getCountryName());
            return addresses.get(0).getCountryName();
        }
        return null;
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

        if (itemId == R.id.action_bookmark) {
             Intent intent = new Intent(getContext(), FavouriteActivity.class);
             startActivity(intent);
        }

        //Reference:https://materialdoc.com/components/dialogs/
        if (itemId == R.id.action_add_alert) {
          showAddAlertDialog(getLayoutInflater(),getActivity(),getContext());
        }

        if (itemId == R.id.action_refresh) {

            if(isConnectedNetwork()){
                //available internet or not
                checkInternetAvailable();

                //refresh earthquake counts, list, and map mark
                refreshScreenData();
            }else {
                Toast.makeText(getContext(),"Internet is not connected",Toast.LENGTH_LONG).show();
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public void showAddAlertDialog(LayoutInflater inflater2, Activity activity, final Context context) {



       // LayoutInflater inflater2 = getLayoutInflater();
        View addAlertView = inflater2.inflate(R.layout.fragment_add_alert, null,false);


        ///Add the location fragment

        final Spinner minMagnitudeSpinner = addAlertView.findViewById(R.id.minOfSpinner);
        final Spinner counteryOfSpinner = addAlertView.findViewById(R.id.regionSelected);

        List<String> countries = getCountriesName();

        //sort the countries name in ascending order (This class Java 8 API Collection framework)
        // to show countries name in spinner a-z
        Collections.sort(countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_spinner_item,
                countries
        );

        counteryOfSpinner.setAdapter(adapter);

        AlertDialog.Builder addAlert = new AlertDialog.Builder(context);
        addAlert.setTitle("Add Alert");
        addAlert.setView(addAlertView);
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

                //TODO: make a list of favorit countery to get notificaiton
                //TODO: then CRUD operation will be necessery (Right Badi ! Yeah why not?)

                //this true will help me to update the adapter in favourite list actitivty

                SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(context);

                //the spinner return the object of selected index so its my duty to convert
                //There could another way to give spinner just double value I did this way
                selectedMinMagnitude = Double.valueOf(
                        String.valueOf(minMagnitudeSpinner.getSelectedItem()));


                selectedCountery = String.valueOf(counteryOfSpinner.getSelectedItem());

//                Log.i(TAG, "onClick: first: "+ selectedMinMagnitude);
//                Log.i(TAG, "onClick: first: "+ selectedCountery);
//                Log.i(TAG, "onClick: store values" + shPref.getString(FAVOURIT_LIST, "null"));

                if (selectedSearchPlaceLatLng != null || selectedSearchPlaceName != null) {
                    selectedCountery = selectedSearchPlaceName;
//                    Log.i(TAG, "place selected entery: " + selectedCountery);
//                    Log.i(TAG, "select longitude: " + selectedSearchPlaceLatLng);
//                    Log.i(TAG, "select longitude: " + selectedSearchPlaceLatLng.latitude);
//                    Log.i(TAG, "select longitude: " + selectedSearchPlaceLatLng.longitude);
                    try {
//                        Log.i(TAG, "get country name: " +
                                getCountryName(context,
                                        Double.valueOf(selectedSearchPlaceLatLng.latitude), Double.valueOf(selectedSearchPlaceLatLng.longitude));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                int indexOfFavritList= 0;

                List<FavoriteCountries> favoriteCountriesList;
                //data stored as a json string in shared preferences
                if (shPref.getString(FAVOURIT_LIST, "null").equals("null")) {

                    //for the first time list create
                    favoriteCountriesList = new ArrayList<>();
                    //first entry add as it is
                    favoriteCountriesList.add(new FavoriteCountries(selectedCountery,selectedMinMagnitude));
                    //converted the object list into json and simply jugar to convert into string :D that's a way
                    shPref.edit().remove(FAVOURIT_LIST).putString(FAVOURIT_LIST,new Gson().toJson(favoriteCountriesList).toString()).commit();

                } else {

                    //get the store string from default shared preferance document
                    String favouritListString = shPref.getString(FAVOURIT_LIST, "null");
                    //parse the string into json and return the list after parsing the string
                    favoriteCountriesList = ParseFavoritCountriesJsonUtils.parseJsonInToList(favouritListString);
                    //check if already exist country then don't add country
                    // if magnitude change then update index
                    if ((indexOfFavritList = isExistInList(selectedCountery, favoriteCountriesList)) != -1) {
                        if (favoriteCountriesList.get(indexOfFavritList).getMagnitude() != selectedMinMagnitude) {

                            favoriteCountriesList.get(indexOfFavritList).setMagnitude(selectedMinMagnitude);

                            shPref.edit().remove(FAVOURIT_LIST).putString(FAVOURIT_LIST,new Gson().toJson(favoriteCountriesList).toString()).apply();

                        }
                    } else {
//                        Log.i(TAG, "onClick: index: "+indexOfFavritList);

                        favoriteCountriesList.add(new FavoriteCountries(selectedCountery, Double.valueOf(selectedMinMagnitude)));
                        shPref.edit().remove(FAVOURIT_LIST).putString(FAVOURIT_LIST,new Gson().toJson(favoriteCountriesList).toString()).apply();
//                        Log.i(TAG, "onClick: size of list 1: " + favoriteCountriesList.size());

                    }
                }

                // Log.i(TAG, "onClick: size of list"+favoriteCountries.size());
            }
        });

        AlertDialog dialog = addAlert.create();
        dialog.show();

        // NotificationsUtils.remindUser(getContext());
    }


    private int isExistInList(String selectedCountery, List<FavoriteCountries> country) {

        for (int index = 0; index < country.size(); index++) {
            if (selectedCountery.equals(country.get(index).getCountry())) {
                return index;
            }
        }

        return -1;
    }

    public boolean isConnectedNetwork(){
        final ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork !=null && activeNetwork.isConnectedOrConnecting();
    }

    private void checkInternetAvailable() {
        final ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork !=null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {

            Snackbar.make(EarthquakeActivity.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Dissmis", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .show();

            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Find a reference to the {@link ListView} in the layout
        // Inflate the layout for this fragment
        // To get the referance we don't have findviewbyId method in fragment so we use view

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        adView1 = view.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        adView1.loadAd(adRequest1);

        adView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);


        adView3 = view.findViewById(R.id.adView3);
        AdRequest adRequest3 = new AdRequest.Builder().build();
        adView3.loadAd(adRequest3);

        latestEarthquakesListView = view.findViewById(R.id.latestEarthquakes);

        latestEarthquakesList = new ArrayList<>();
        loadLatestEarthquakes();

        earthListAdapter = new EarthQuakeAdapter(getContext(), R.layout.listview_item, latestEarthquakesList);
        latestEarthquakesListView.setAdapter(earthListAdapter);

        latestEarthquakesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                      @Override
                                                      public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                                          showMapActivity(position, latestEarthquakesList);
                                                      }
                                                  });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Mapfragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //cast (TextView) is redendent
        todayEarthquakes = view.findViewById(R.id.magnitude);
        yesterdayEarthquakes = view.findViewById(R.id.yesterday);
        thisWeekEarthquakes = view.findViewById(R.id.week);
        thisMonthEarthquakes = view.findViewById(R.id.month);
        todayMapStatus = view.findViewById(R.id.today_mapStatus);
        //Find a reference to the {@link Progressbar} int the layout
        progressBar = view.findViewById(R.id.progressbar);

        setInitialValues();

        // java.util.calender class
        // REFERENCE: https://stackoverflow.com/questions/3747490/android-get-date-before-7-days-one-week

        // Date of 7 days back
        Date newDateForWeek = getDate(7);

        //Made the 1st date of this month
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date newDateForMonth = calendar1.getTime();

        //Date for previous day
        Date newDateForYesterday = getDate(1);

        String yesterdayDate = DataProviderFormat.getformateDate(newDateForYesterday);
        String weekDate = DataProviderFormat.getformateDate(newDateForWeek);
        String monthDate = DataProviderFormat.getformateDate(newDateForMonth);

        /**
         * This is very importanted to be notice why I'm just extracting dates and concatenating with this
         * URL somthing odd right! Badi did this bcz in USA I don't know why the USGS considering the 'allday'
         * as from 5am to 5am e.g 2017-12-12 05:00 am to 2017-12-13 05:00 am.
         *
         * Experiment on this link: https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson
         * Date: 2018-01-03
         * Website used to convert timestamp: https://www.epochconverter.com/batch#results
         * Comparision Values: 1st index of  features-> [index 1] property -> [index 3] = time
         *                      (Timestamp=1514956505260  Converted into  Date=2018-01-03 05:15:05)
         *                      Last index of  features-> [index 1] property -> [index 2] = time
         *                      Timestamp=1514870465160 Converted into 2018-01-02 05:21:05
         *
         * Conclusion: The purpose is to show the user today its mean 12:00 am to 12:00 am
         *              So, the values will be get as an instance from the java class fro cell phone (correct me if
         *              im wrong) and then it I concatenate with URL and fetch TODAY earthquakes! :)
         * */

        //today date concatenate url string
        countURLS[0] = CountURL + todayDate;

        //yesterday date concatenate url string
        countURLS[1] = CountURL + yesterdayDate;

        //week concatenate url string
        countURLS[2] = CountURL + weekDate;

        //month concatenate url string
        countURLS[3] = CountURL + monthDate;

        //get the counts eathquakes
        dataFetch(countURLS);
        displayProgressBar(true);

        //Show today Earthqukes on the home map

        //My location
        fusedLocationProviderClient = new FusedLocationProviderClient(getContext());

        createHandlerMessage();

        earthQuakesArrayList = new ArrayList<>();

        //TODO:If time allow me I will add some more information on click the circles
        //Add listeners to the Circles and Textviews
        todayEarthquakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Today");
                showEarthquakesOnMap(TODAY);

            }
        });
        yesterdayEarthquakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Yesterday");
                showEarthquakesOnMap(YESTERDAY);
            }
        });
        thisWeekEarthquakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Week");
                showEarthquakesOnMap(WEEK);
            }
        });
        thisMonthEarthquakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEarthquakesOnMap(MONTH);
                Log.i(TAG, "onClick: Month");
            }
        });

        return view;
    }

    private void showEarthquakesOnMap(final String urlType) {
        //Remember badar you faced problem using runnble and runOnUithread while sending request
        //to the network. The asynctask work

        //progress bar to show user the work is started
        displayProgressBar(true);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                EarthquakeLoader.HttpHandler ob = new EarthquakeLoader.HttpHandler();
                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(
                        TimelineFragment.getDateFormatedURLString(urlType, URL));

                // Log.i(TAG, "doInBackground: json " + jsonResponse.toString());
                ParseUSGSJsonUtils parseUSGSJsonUtils = new ParseUSGSJsonUtils();
                earthQuakesArrayList = parseUSGSJsonUtils.parseJsonIntoData(earthQuakesArrayList, jsonResponse, getContext(), false);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                showMapActivity(earthQuakesArrayList);
            }
        };
        task.execute();

    }

    private void showMapActivity(int position,List<EarthQuakes> earthQuakesArrayList) {
        Intent intent = new Intent(getContext(), Map.class);

        Bundle bundle = new Bundle();
        bundle.putString(TimelineFragment.MARK_TYPE, TimelineFragment.SINGLE);
        bundle.putDouble(TimelineFragment.LONGITUDE, earthQuakesArrayList.get(position).getLongitude());
        bundle.putDouble(TimelineFragment.LATITUDE, earthQuakesArrayList.get(position).getLatitude());
        bundle.putString(TimelineFragment.CITY, earthQuakesArrayList.get(position).getCityname());
        bundle.putString(TimelineFragment.MAGNITUDE, earthQuakesArrayList.get(position).getMagnitude());
        bundle.putString(TimelineFragment.DATE, earthQuakesArrayList.get(position).getTimeStamp());

        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void showMapActivity(List<EarthQuakes> earthQuakesArrayList) {
        try {
            Intent intent = new Intent(getContext(), Map.class);
            Bundle bundle = new Bundle();
            bundle.putString(TimelineFragment.MARK_TYPE, TimelineFragment.MULTIPLE);
            bundle.putSerializable(TimelineFragment.DATA, (Serializable) earthQuakesArrayList);
            intent.putExtras(bundle);

            //when everything is setup then disable proress bar
            displayProgressBar(false);
            startActivity(intent);
        }catch (Exception e){
            Log.e(TAG, "showMapActivity: "+ e );
        }
    }

    private void setInitialValues() {
        String today = sharedPreferences.getString(TODAY_COUNT_KEY, "34");
        String yesterday = sharedPreferences.getString(YESTERDAY_COUNT_KEY, "100");
        String week = sharedPreferences.getString(WEEK_COUNT_KEY, "780");
        String month = sharedPreferences.getString(MONTH_COUNT_KEY, "3100");

        //set the number of earthquakes
        todayEarthquakes.setText(today);
        yesterdayEarthquakes.setText(yesterday);
        thisWeekEarthquakes.setText(week);
        thisMonthEarthquakes.setText(month);

        todayMapStatus.setText("Today " + today + " Earthquakes occur");
    }

    private void createHandlerMessage() {
        // After fetching the number of earthquakes set in the views
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                //TODO: If net is not available handle this in network package
                //But progressbar issue?

                Bundle bundle = msg.getData();
                int countKey[] = bundle.getIntArray(COUNT_KEY);

                for (int a = 0; a < countKey.length; a++) {
                    COUNT_KEY_ARRAY[a] = String.valueOf(countKey[a]);
                }


                sharedPreferences.edit().putString(TODAY_COUNT_KEY, COUNT_KEY_ARRAY[0]).apply();
                sharedPreferences.edit().putString(YESTERDAY_COUNT_KEY, COUNT_KEY_ARRAY[1]).apply();
                sharedPreferences.edit().putString(WEEK_COUNT_KEY, COUNT_KEY_ARRAY[2]).apply();
                sharedPreferences.edit().putString(MONTH_COUNT_KEY, COUNT_KEY_ARRAY[3]).apply();

                String today = sharedPreferences.getString(TODAY_COUNT_KEY, "34");

                setInitialValues();

                // Log.i(TAG, "month earthquakes: "+COUNT_KEY_ARRAY[3]);
                todayMapStatus.setText("Today " + COUNT_KEY_ARRAY[0] + " Earthquakes occur");
                //Stop the progressbar and hide
                displayProgressBar(false);
            }
        };
    }

    //TODO: Data fetch time out then hide progress bar and show notificaiton
    private void displayProgressBar(boolean visiblity) {
        if (visiblity) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //TODO: We can move datafetch method as class in network package
    //count the earthquakes
    private void dataFetch(final String[] URLS) {

        //start the progressbar and visible

        final int[] count = new int[URLS.length];
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (int a = 0; a < count.length; a++) {

                    String jasonStr = EarthquakeLoader.HttpHandler.makeServeiceCall(URLS[a]);

                    //if the internet available and the jason data receive in jasonStr then
                    if (jasonStr != null) {

                        //get json string values as an object
                        JSONObject root = null;

                        try {
                            root = new JSONObject(jasonStr);
                            count[a] = root.getInt("count");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //if today is and yesterday result get empty then show previous data
                if (count[0] == 0 && count[1] == 0) {
                    //TODO:previoud sharedpreference data
                }

                //set the text of quakes count
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putIntArray(COUNT_KEY, count);
                message.setData(bundle);

                if(handler!=null){
                    handler.sendMessage(message);
                }else{
                    createHandlerMessage();
                    handler.sendMessage(message);
                }

            }
        };

        Thread th = new Thread(runnable);
        th.start();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {


        alldayList = new ArrayList<>();
        AsyncTask background;

        //Going to get the today earthquakes
        background = new AsyncTask() {


            @Override
            protected Object doInBackground(Object[] objects) {

//                Log.i(TAG, "going to get home json response");
                //Why I didn't do this to count the today earthquake give to count well it was possible but I do this way
                //Today URL of the earthquakes
                //Get the today date 00:00  to today date 23:59 furthur could understand in getDateFormatedURL method
                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(
                        TimelineFragment.getDateFormatedURL(todayDate, todayDate));

//                Log.i(TAG, "json response of home: "+jsonResponse);

                boolean isFiltered = false;

                // Log.i(TAG, "doInBackground: json " + jsonResponse.toString());
                ParseUSGSJsonUtils parseUSGSJsonUtils = new ParseUSGSJsonUtils();
                alldayList = parseUSGSJsonUtils.parseJsonIntoData(alldayList, jsonResponse, getContext(), isFiltered);
                // Log.i(TAG, "doInBackground: " + alldayList.size());
//                Log.i(TAG, "doInBackground: of maps mark working" + alldayList.size());
                //if I return null then alldayList on main thread null
                return null;

            }

            List<Marker> markerList = new ArrayList<>();
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (alldayList != null) {

                    LatLngBounds.Builder builder= new LatLngBounds.Builder();
                    for (int a = 0; a < alldayList.size(); a++) {
                        LatLng location = new LatLng(alldayList.get(a).getLatitude(), alldayList.get(a).getLongitude());

                       Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(alldayList.get(a).getCityname())
                                .snippet("Magnitude:" + alldayList.get(a).getMagnitude() +
                                        " Date:" + DataProviderFormat.getformateDate(
                                        new Date(
                                                Long.valueOf(alldayList.get(a).getTimeStamp())
                                        ))
                                )
                        );


                       markerList.add(marker);
                    }


                    if(!markerList.isEmpty()) {
                        for (Marker marker : markerList) {
                            builder.include(marker.getPosition());
                        }

                        LatLngBounds bounds = builder.build();
                        int padding = 0;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.animateCamera(cu);
                    }else{
                        return;
                    }
                }

            }
        };

        background.execute();

/*
        googleMap.addCircle(new CircleOptions().center(new LatLng(31,72))
                .radius(2000)
                .fillColor(Color.RED)
                .strokeWidth(100));
*/
//        Log.i(TAG, "onMapReady: and size of alldayList: " + alldayList.size());

        //Previously I got the today earthquakes now I'm gonna add marks on the map

        //TODO: give user facility in the future to set differen to check different maps

        /* googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        */
    }

    public Date getDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -day);
        Date newDateForWeek = calendar.getTime();
        return newDateForWeek;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
        handlerLatest = new Handler();
        isScreeVisible = true;

        super.onStart();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(isConnectedNetwork()){
                //show the add after 30 seconds
                EarthquakeActivity.showAd();
                refreshScreenData();
            }else {
                Toast.makeText(getContext(),"Internet is not connected",Toast.LENGTH_LONG).show();
            }

            handlerLatest.postDelayed(this,20000);
        }
    };

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");

        if(adView1 !=null){
            adView1.resume();
        }

        if(adView2 !=null){
            adView2.resume();
        }

        if(adView3 !=null){
            adView3.resume();
        }

        handlerLatest = new Handler();
        isScreeVisible = true;
        handlerLatest.postDelayed(runnable,20000);

        super.onResume();
    }

    public void loadLatestEarthquakes(){

        AsyncTask background;


        //Going to get the today earthquakes
        background = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {


                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(
                        HOUR_URL);


                ParseUSGSJsonUtils parseUSGSJsonUtils = new ParseUSGSJsonUtils();
                latestEarthquakesList.clear();
                latestEarthquakesList = parseUSGSJsonUtils.parseJsonIntoData(latestEarthquakesList , jsonResponse, getContext(), false);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (latestEarthquakesList != null) {
                    Log.i("Latest", "Size of Latest: "+latestEarthquakesList .size());

                    earthListAdapter.clear();
                    earthListAdapter.addAll(latestEarthquakesList);

                }

            }
        };

        background.execute();

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause: ");

        if(adView1 !=null){
            adView1.pause();
        }

        if(adView2 !=null){
            adView2.pause();
        }

        if(adView3 !=null){
            adView3.pause();
        }

        isScreeVisible = false;
        handlerLatest.removeCallbacks(runnable);
        handlerLatest = null;

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        isScreeVisible = false;
        handlerLatest = null;

        super.onStop();
    }

    void refreshScreenData() {

        //get the latest earthquakes in this hour
        loadLatestEarthquakes();
        
        //get the counts eathquakes
        dataFetch(countURLS);
        displayProgressBar(true);

        //set map values
        //Mapfragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



}
