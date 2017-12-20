package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.earthreport.R;
import com.example.android.earthreport.ShowEarthquakeDetails;
import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.network.EarthquakeLoader;
import com.example.android.earthreport.notifications.NotificationsUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();
    public static String TODAY_COUNT_KEY = "todayCountkey";
    public static String YESTERDAY_COUNT_KEY = "yesterdayCountkey";
    public static String WEEK_COUNT_KEY = "weekCountkey";
    public static String MONTH_COUNT_KEY = "monthCountkey";
    public static Handler handler;

    public TextView todayEarthquakes;
    public TextView yesterdayEarthquakes;
    public TextView thisMonthEarthquakes;
    public TextView thisWeekEarthquakes;

    private String[] countURLS = new String[4];
    // private ProgressBar progressBar;
    //This hour earthQuakes url (query to get values) | Below I concatenate the date for todady
    private String thisHourURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    //Today earthquakes count
    private String CountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";
    private Date dateForWeek;

    public HomeFragment() {

    }

    View.OnClickListener showDataList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getContext(), ShowEarthquakeDetails.class);
            startActivity(intent);
        }
    };

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
        todayEarthquakes = view.findViewById(R.id.today);
        yesterdayEarthquakes = view.findViewById(R.id.yesterday);
        thisWeekEarthquakes = view.findViewById(R.id.week);
        thisMonthEarthquakes = view.findViewById(R.id.month);

        //Find a reference to the {@link Progressbar} int the layout
        //  progressBar = view.findViewById(R.id.progress_bar);

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

        String todayDate = DataProvider.getformateDate(new Date());
        String yesterdayDate = DataProvider.getformateDate(newDateForYesterday);
        String weekDate = DataProvider.getformateDate(newDateForWeek);
        String monthDate = DataProvider.getformateDate(newDateForMonth);

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

        // After fetching the number of earthquakes set in the views
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                //TODO: If net is not available handle this in network package
                //But progressbar issue?

                Bundle bundle = msg.getData();

                String todayCount = bundle.getString(TODAY_COUNT_KEY);
                String yesterdayCount = bundle.getString(YESTERDAY_COUNT_KEY);
                String weekCount = bundle.getString(WEEK_COUNT_KEY);
                String monthCount = bundle.getString(MONTH_COUNT_KEY);

                //set the number of earthquakes
                todayEarthquakes.setText(todayCount);
                yesterdayEarthquakes.setText(yesterdayCount);
                thisWeekEarthquakes.setText(weekCount);
                thisMonthEarthquakes.setText(monthCount);

                //Stop the progressbar and hide
                // displayProgressBar(false);
            }
        };

        //Add listeners to the Circles and Textviews
        todayEarthquakes.setOnClickListener(showDataList);
        yesterdayEarthquakes.setOnClickListener(showDataList);
        thisWeekEarthquakes.setOnClickListener(showDataList);
        thisMonthEarthquakes.setOnClickListener(showDataList);

        return view;
    }


    //TODO: We can move datafetch method as class in network package
    //count the earthquakes
    private void dataFetch(final String[] URLS) {

        //start the progressbar and visible

        Log.i(TAG, "dataFetch: ");
        final String keys[] = new String[]{TODAY_COUNT_KEY,YESTERDAY_COUNT_KEY,WEEK_COUNT_KEY
        ,MONTH_COUNT_KEY};
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

                            //set the text of quakes count
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString(keys[a], String.valueOf(count[a]));
                            message.setData(bundle);
                            handler.sendMessage(message);
                            Log.i(TAG, "dataFetch: send back");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


            }
        };

        Thread th = new Thread(runnable);
        th.start();

        Log.i(TAG, "thread started ");
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

            //Fetch today earthquakes count
            dataFetch(countURLS);

            //show notification
            NotificationsUtils.remindUser(getContext());
        }

        return super.onOptionsItemSelected(item);
    }

    //Life Cycle

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public Date getDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -day);
        Date newDateForWeek = calendar.getTime();
        return newDateForWeek;
    }
}
