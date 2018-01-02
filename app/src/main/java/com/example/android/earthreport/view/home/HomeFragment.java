package com.example.android.earthreport.view.home;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.api.EarthquakeLoader;
import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.model.pojos.FavoriteCountries;
import com.example.android.earthreport.model.utilties.DataProviderFormat;
import com.example.android.earthreport.model.utilties.FavoritCountriesUtilties;
import com.example.android.earthreport.view.search.SearchEarthquakeActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    public static final String FAVOURIT_LIST = "favouritList";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String COUNT_KEY = "countKey";
    public static String TODAY_COUNT_KEY = "todayCountkey";
    public static String YESTERDAY_COUNT_KEY = "yesterdayCountkey";
    public static String WEEK_COUNT_KEY = "weekCountkey";
    public static String MONTH_COUNT_KEY = "monthCountkey";
    private static final String COUNT_KEY_ARRAY[] = {TODAY_COUNT_KEY, YESTERDAY_COUNT_KEY, WEEK_COUNT_KEY, MONTH_COUNT_KEY};
    public static String ALL_DAY_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    public static Handler handler;
    public String selectedMin;
    public String selectedCountery;

    private TextView todayEarthquakes;
    private TextView yesterdayEarthquakes;
    private TextView thisMonthEarthquakes;
    private TextView thisWeekEarthquakes;
    private TextView todayMapStatus;
    private Spinner minMagnitudeSpinner;
    private Spinner counteryOfSpinner;
    private String[] countURLS = new String[4];
    private ProgressBar progressBar;
    private List<FavoriteCountries> favoriteCountries;
    private List<EarthQuakes> alldayList;


    //This hour earthQuakes url (query to get values) | Below I concatenate the date for todady
    private String thisHourURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    //Today earthquakes count
    private String CountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";

    public HomeFragment() {

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
                Log.i(TAG, "Get Display Countery" + obj.getDisplayCountry());
                Log.i(TAG, "Countery Name: " + obj.getDisplayCountry());
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

        if (itemId == R.id.action_search) {
            Intent intent = new Intent(getContext(), SearchEarthquakeActivity.class);
            startActivity(intent);
        }


        //Reference:https://materialdoc.com/components/dialogs/
        if (itemId == R.id.action_add_alert) {

            //TODO:Countery name with longitude and latitude have be doen more
            // have to do with service and data comparison and give notification
/*
            if(isCounteryExist(getContext(), -4.3777,101.9364)){
                Log.i(TAG, "onCreate: Now you are ready!");
            }
            if(isCounteryExist(getContext(), 36.9294,71.3741)){
                Log.i(TAG, "onCreate: Now you are ready!");
            }
            if(isCounteryExist(getContext(), -5.4215,-80.4563)){
                Log.i(TAG, "onCreate: Now you are ready!");
            }
*/

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_add_alert, null);

            minMagnitudeSpinner = view.findViewById(R.id.minOfSpinner);
            counteryOfSpinner = view.findViewById(R.id.countryOfSpinner);


            List<String> countries = getCountriesName();
            //sort the countries name to show in spinner a-z
            Collections.sort(countries);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_spinner_item,
                    countries
            );

            counteryOfSpinner.setAdapter(adapter);

            AlertDialog.Builder addAlert = new AlertDialog.Builder(getContext());
            addAlert.setTitle("Add Alert");
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

                    //TODO: make a list of favorit countery to get notificaiton
                    //TODO: then CRUD operation will be necessery (Right Badi ! Yeah why not?)

                    selectedMin = String.valueOf(minMagnitudeSpinner.getSelectedItem());
                    selectedCountery = String.valueOf(counteryOfSpinner.getSelectedItem());

                    SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = shPref.edit();


                    //Log.i(TAG, "onClick: 1 "+shPref.getString(FAVOURIT_LIST,"null"));
                    if (shPref.getString(FAVOURIT_LIST, "null").equals(null)) {

                        favoriteCountries = new ArrayList<>();


                        favoriteCountries.add(new FavoriteCountries(selectedCountery, Double.valueOf(selectedMin)));
                        editor.putString(FAVOURIT_LIST, new Gson().toJson(favoriteCountries).toString());
                        editor.apply();

                        Log.i(TAG, "onClick: 2 " + new Gson().toJson(favoriteCountries).toString());

                    } else {

                        favoriteCountries = new ArrayList<>();

                        String favouritListString = shPref.getString(FAVOURIT_LIST, "null");
                        //This will return the list of objects of favrit countries
                        favoriteCountries = FavoritCountriesUtilties.parseJsonInToList(favouritListString);
                        favoriteCountries.add(new FavoriteCountries(selectedCountery, Double.valueOf(selectedMin)));

                        /**
                         * Title: How do I remove repeated elements from ArrayList?
                         * Author: jonathan-stafford
                         * Date: 2017-12-20
                         * Code version: N/A
                         * Availability: https://stackoverflow.com/questions/203984/how-do-i-remove-repeated-elements-from-arraylist
                         //Remove the duplicaties entries
                         */

                        List<FavoriteCountries> al = favoriteCountries;
                        Set<FavoriteCountries> hs = new HashSet<>();

                        hs.addAll(al);
                        al.clear();
                        al.addAll(hs);

                        favoriteCountries = al;
                        editor.putString(FAVOURIT_LIST, new Gson().toJson(favoriteCountries).toString());
                        editor.apply();
                        Log.i(TAG, "onClick: 3 " + new Gson().toJson(favoriteCountries).toString());

                    }


                    //String countary = shPref.getString("favouritList",null);
                    // List<FavoriteCountries> list =

//                    Log.i(TAG, "onClick: "+selectedMin);
//                    Log.i(TAG, "onClick: "+selectedCountery);
//                    Toast.makeText(getContext(),"Done",Toast.LENGTH_SHORT).show();

                }
            });

            AlertDialog dialog = addAlert.create();
            dialog.show();
            // NotificationsUtils.remindUser(getContext());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Find a reference to the {@link ListView} in the layout
        // Inflate the layout for this fragment
        // To get the referance we don't have findviewbyId method in fragment so we use view
        View view = inflater.inflate(R.layout.fragment_home, container, false);


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

        // java.util.calender class
        // REFERENCE: https://stackoverflow.com/questions/3747490/android-get-date-before-7-days-one-week

        // Date of 7 days back
        Date newDateForWeek = getDate(7);
        Log.i(TAG, "Seven day back " + newDateForWeek);

        //Made the 1st date of this month
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date newDateForMonth = calendar1.getTime();
        Log.i(TAG, "Month date " + newDateForMonth);

        //Date for previous day
        Date newDateForYesterday = getDate(1);
        Log.i(TAG, "newDateForYesterday: " + newDateForYesterday);

        String todayDate = DataProviderFormat.getformateDate(new Date());
        String yesterdayDate = DataProviderFormat.getformateDate(newDateForYesterday);
        String weekDate = DataProviderFormat.getformateDate(newDateForWeek);
        String monthDate = DataProviderFormat.getformateDate(newDateForMonth);

        Log.i(TAG, "newDateForYesterday  in Format: " + yesterdayDate);

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


        alldayList = new ArrayList<>();
        //Show today Earthqukes on the home map


        AsyncTask background;

        background = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(ALL_DAY_URL);
                Log.i(TAG, "doInBackground: json " + jsonResponse.toString());
                alldayList = EarthquakeLoader.parseJsonIntoData(alldayList, jsonResponse, getContext());
                Log.i(TAG, "doInBackground: " + alldayList.size());

                return null;
            }
        };

        background.execute();
        



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

                //set the number of earthquakes
                todayEarthquakes.setText(COUNT_KEY_ARRAY[0]);
                yesterdayEarthquakes.setText(COUNT_KEY_ARRAY[1]);
                thisWeekEarthquakes.setText(COUNT_KEY_ARRAY[2]);
                thisMonthEarthquakes.setText(COUNT_KEY_ARRAY[3]);

                todayMapStatus.setText("Today " + COUNT_KEY_ARRAY[0] + " Earthquakes occure");
                //Stop the progressbar and hide
                displayProgressBar(false);
            }
        };

        //Add listeners to the Circles and Textviews
        //TODO:If time allow me I will add some more information on click the circles
//        todayEarthquakes.setOnClickListener(showDataList);
//        yesterdayEarthquakes.setOnClickListener(showDataList);
//        thisWeekEarthquakes.setOnClickListener(showDataList);
//        thisMonthEarthquakes.setOnClickListener(showDataList);

        return view;
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

                Log.i(TAG, "dataFetch: thread started");

                for (int a = 0; a < count.length; a++) {

                    Log.i(TAG, "run: counting");
                    String jasonStr = EarthquakeLoader.HttpHandler.makeServeiceCall(URLS[a]);
                    Log.i(TAG, "request send");
                    //if the internet available and the jason data receive in jasonStr then
                    if (jasonStr != null) {

                        //get json string values as an object
                        JSONObject root = null;

                        try {
                            root = new JSONObject(jasonStr);
                            count[a] = root.getInt("count");
                            // Log.i("COUNT", count[0] + "");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                //set the text of quakes count
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putIntArray(COUNT_KEY, count);
                message.setData(bundle);
                handler.sendMessage(message);

//                Log.i(TAG, "dataFetch: send back");

            }
        };

        Thread th = new Thread(runnable);
        th.start();

        Log.i(TAG, "thread started ");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(TAG, "onMapReady: ");
        if (alldayList != null) {
            for (int a = 0; a < alldayList.size(); a++) {
                LatLng location = new LatLng(alldayList.get(a).getLatitude(), alldayList.get(a).getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(alldayList.get(a).getCityname())
                        .snippet("Magnitude:" + alldayList.get(a).getMagnitude() +
                                " Date:" + DataProviderFormat.getformateDate(
                                new Date(
                                        Long.valueOf(alldayList.get(a).getDate())
                                ))
                        )
                );
                Log.i(TAG, "onMapReady: calling");
            }
        }

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


}
