package com.example.android.earthreport.view.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.api.EarthquakeLoader;
import com.example.android.earthreport.model.utilties.DataProviderFormat;
import com.example.android.earthreport.view.addalert.AddAlertDialog;
import com.example.android.earthreport.view.search.SearchEarthquakeActivity;
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
    private static final String COUNT_KEY = "countKey";
    public static String TODAY_COUNT_KEY = "todayCountkey";
    public static String YESTERDAY_COUNT_KEY = "yesterdayCountkey";
    public static String WEEK_COUNT_KEY = "weekCountkey";
    public static String MONTH_COUNT_KEY = "monthCountkey";
    private static final String COUNT_KEY_ARRAY[] = {TODAY_COUNT_KEY, YESTERDAY_COUNT_KEY, WEEK_COUNT_KEY, MONTH_COUNT_KEY};
    public static Handler handler;
    public TextView todayEarthquakes;
    public TextView yesterdayEarthquakes;
    public TextView thisMonthEarthquakes;
    public TextView thisWeekEarthquakes;
    View.OnClickListener showDataList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getContext(), SearchEarthquakeActivity.class);
            startActivity(intent);
        }
    };
    private String[] countURLS = new String[4];
    private ProgressBar progressBar;
    //This hour earthQuakes url (query to get values) | Below I concatenate the date for todady
    private String thisHourURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    //Today earthquakes count
    private String CountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";
    private Date dateForWeek;
    private SwipeRefreshLayout swipeRefresh;

    public HomeFragment() {

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
/*
        //Referesh will update the earthquakes count and hourly earthquakes list
        if (itemId == R.id.action_refresh) {

            //Fetch today earthquakes count
            dataFetch(countURLS);

            //refresh progress bar
            progressBar.setEnabled(true);

            //show notification
            NotificationsUtils.remindUser(getContext());
        }*/

        if (itemId == R.id.action_search) {
            Intent intent = new Intent(getContext(), SearchEarthquakeActivity.class);
            startActivity(intent);
        }

        if(itemId == R.id.action_add_alert){

            AddAlertDialog alertDialog = new AddAlertDialog();
            alertDialog.show(getFragmentManager(),"action_alert");
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
                Log.i(TAG, "dataFetch: send back");
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
    public void onMapReady(GoogleMap googleMap) {

        //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

       /* LatLng latLng = new LatLng(69.3451,30.3753);
        googleMap.addCircle(new CircleOptions()
        .visible(true)
        .center(latLng)
        .radius(1000)
        .fillColor(Color.parseColor("#FF2343"))
        );

*/

        //TODO: give user facility to set the map type check this one
       /* googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


*/


        //This is to add image overlay source :https://developers.google.com/maps/documentation/android-api/groundoverlay
/*
        private GoogleMap mMap;
        private GroundOverlay mSydneyGroundOverlay;

        mSydneyGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
                .position(new LatLng(-33.873, 151.206))
                .clickable(true));

        mSydneyGroundOverlay.setTag("Sydney");*/
    }

    public Date getDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -day);
        Date newDateForWeek = calendar.getTime();
        return newDateForWeek;
    }


}
