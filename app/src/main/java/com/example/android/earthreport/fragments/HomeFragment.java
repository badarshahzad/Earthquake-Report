package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.ShowEarthquakeDetails;
import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.GetEarthquakeData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    public static String TODAY_COUNT_KEY = "todayCountkey";
    public static String WEEK_COUNT_KEY = "weekCountkey";
    public static String MONTH_COUNT_KEY = "monthCountkey";
    public static Handler handler;
    public TextView todayEarthquakes;
    public TextView thisMonthEarthquakes;
    public TextView thisWeekEarthquakes;
    ArrayList<EarthQuakes> earthQuakesList;
    String[] countURLS = new String[3];
    View.OnClickListener showDataList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getContext(), ShowEarthquakeDetails.class);
            startActivity(intent);
        }
    };
    private ProgressBar progressBar;
    private ListView earthquakeListView;
    //This hour earthQuakes url (query to get values) | Below I concatenate the date for todady
    private String thisHourURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    //Today earthquakes count
    private String todayEarthquakesCountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";
    //Week earthquakes count
    private String weekEarthquakesCountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";
    //Month earthquakes count
    private String monthEarthquakesCountURL = "https://earthquake.usgs.gov/fdsnws/event/1/count?format=geojson&starttime=";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*private void retrofiDataFetch() {

        MyWebService myWebService = MyWebService.retrofit.create(MyWebService.class);

        Call<List<EarthQuakes>> call = myWebService.earquakesCount();
        call.enqueue(new Callback<List<EarthQuakes>>() {
            @Override
            public void onResponse(Call<List<EarthQuakes>> call, Response<List<EarthQuakes>> response) {

                Log.i(TAG, "onResponse: " + response.body().toString());

//                if (response.isSuccessful()) {
//
//                    Log.i(TAG, "onResponse: " + response.body().toString());
//                }
            }

            @Override
            public void onFailure(Call<List<EarthQuakes>> call, Throwable t) {

                //on network failure handling
                Log.i(TAG, "failure :(");
            }
        });
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Find a reference to the {@link ListView} in the layout
        // Inflate the layout for this fragment
        // To get the referance we don't have findviewbyId method in fragment so we use view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //cast (TextView) is redendent
        todayEarthquakes = view.findViewById(R.id.todayEarthquakes);
        thisWeekEarthquakes = view.findViewById(R.id.thisWeekEarthquakes);
        thisMonthEarthquakes = view.findViewById(R.id.thisMonthEarthquakes);

        //Find a reference to the {@link Progressbar} int the layout
        progressBar = view.findViewById(R.id.progress_bar);

        // Find a reference to the {@link ListView} in the layout
        // The application is working fine without casting?
        // Honestly I didn't add any external library yet
        earthquakeListView = view.findViewById(R.id.dataList);

     /*   //----------Checking Retrofit------------
        //  retrofiDataFetch();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(todayEarthquakesCountURL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        GitHubClient client = retrofit.create(GitHubClient.class);
        Call<List<GitHubRepo>> call = client.reposForUser("fs-opensource");
        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                //actuallyy got a response
              Toast.makeText(getContext(),"Data Fetched:"+response.body().get(0),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                //on internet failure
                Toast.makeText(getContext(),"Error: Network Failure",Toast.LENGTH_SHORT).show();
            }
        });
       // -----------*/

        // Data replicate in listview due to creation of activity
        // I added for just when view appear listview
        // instance recereate and assigned (check):
        earthQuakesList = new ArrayList<>();


        // java.util.calender class
        // REFERENCE: https://stackoverflow.com/questions/3747490/android-get-date-before-7-days-one-week
        // Made the date 7 days back
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date newDateForWeek = calendar.getTime();

        //Made the 1st date of this month
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date newDateForMonth = calendar1.getTime();


        String todayDate = DataProvider.getformateDate(new Date());
        String weekDate = DataProvider.getformateDate(newDateForWeek);
        String monthDate = DataProvider.getformateDate(newDateForMonth);

        // Date is concatenating with url
        // thisHourURL = thisHourURL + date;

        //today date concatenate url string
        countURLS[0] = todayEarthquakesCountURL + todayDate;

        //week concatenate url string
        countURLS[1] = weekEarthquakesCountURL + weekDate;

        //month concatenate url string
        countURLS[2] = monthEarthquakesCountURL + monthDate;


        //Fetch today earthquakes count
        //  GetEarthquakeCount getEQC = new GetEarthquakeCount();
        //  getEQC.dataFetch(countURLS);

        //or
        dataFetch(countURLS);

        // After fetching the number of earthquakes set in the views
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();

                String todayCount = bundle.getString(TODAY_COUNT_KEY);
                String weekCount = bundle.getString(WEEK_COUNT_KEY);
                String monthCount = bundle.getString(MONTH_COUNT_KEY);

                //set the number of earthquakes
                todayEarthquakes.setText(todayCount);
                thisWeekEarthquakes.setText(weekCount);
                thisMonthEarthquakes.setText(monthCount);

                //Stop the progressbar and hide
                displayProgressBar(false);
            }
        };

        // Get data from web of this hour earthquakes
        GetEarthquakeData getEarthquakeData = new GetEarthquakeData(getContext(), earthquakeListView, earthQuakesList);
        getEarthquakeData.execute(thisHourURL);


        //Add listeners to the Circles and Textviews
        todayEarthquakes.setOnClickListener(showDataList);
        thisWeekEarthquakes.setOnClickListener(showDataList);
        thisMonthEarthquakes.setOnClickListener(showDataList);

        //Add list view listener to open detail activity of each list view value
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

    //TODO: We can move datafetch method as class in network package
    //count the earthquakes
    private void dataFetch(final String[] URLS) {

        //start the progressbar and visible
        displayProgressBar(true);

        final int[] count = new int[3];
        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                for (int a = 0; a < count.length; a++) {

                    String jasonStr = GetEarthquakeData.HttpHandler.makeServeiceCall(URLS[a]);
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
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(TODAY_COUNT_KEY, String.valueOf(count[0]));
                bundle.putString(WEEK_COUNT_KEY, String.valueOf(count[1]));
                bundle.putString(MONTH_COUNT_KEY, String.valueOf(count[2]));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };

        Thread th = new Thread(runnable);
        th.start();
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
            Toast.makeText(getContext(), "Home Update", Toast.LENGTH_SHORT).show();

            //there could be many other ways to update the listview values what I did this below
            //Referesh menu click and values again fetch and update the listview
            new GetEarthquakeData(getContext(), earthquakeListView, earthQuakesList).execute(thisHourURL);

            //Fetch today earthquakes count
            dataFetch(countURLS);
        }

        return super.onOptionsItemSelected(item);
    }

}
