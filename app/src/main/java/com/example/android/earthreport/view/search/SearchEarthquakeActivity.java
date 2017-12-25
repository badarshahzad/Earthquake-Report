package com.example.android.earthreport.view.search;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.example.android.earthreport.view.map.Map;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.AdapterView.OnClickListener;

public class SearchEarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuakes>>, DatePickerDialog.OnDateSetListener {


    public static final String TAG = SearchEarthquakeActivity.class.getSimpleName();

    public static final int NO_DATA_ERROR = R.drawable.ic_error_colored_24dp;
    public static final int NO_INTERNET_ERROR = R.drawable.ic_cloud_off_black_24dp;
    public static final String NO_DATA_TEXT = "No data retrieve check Filter or Start and End Date in Setting.";
    public static final String NO_INTERNET_TEXT = "No Internet Connection";
    public static final String DATE = "DATE";
    public static ConstraintLayout root;
    public final String MARK_TYPE = "MARK_TYPE";
    public final String LONGITUDE = "LONGITUDE";
    public final String LATITUDE = "LATITUDE";
    public final String CITY = "CITY";
    public final String MAGNITUDE = "MAGNITUDE";
    public String selectedStartDate;
    public String selectedEndDate;
    private Spinner minMagnitudeSpinner;
    private Spinner maxMagnitudeSpinner;
    private Spinner orderBySpinner;
    private TextView emptyStateText;
    private ImageView emptyStateImagView;
    private ImageButton startDate;
    private ImageButton endDate;
    private ListView listView;
    private List<EarthQuakes> earthQuakesArrayList;
    private EarthQuakeAdapter earthListAdapter;
    private DatePickerDialog datePickerDialog;
    private boolean start = true;


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_earthquake_details);
        getSupportActionBar().setHomeButtonEnabled(true);

        root = findViewById(R.id.root);

        minMagnitudeSpinner = findViewById(R.id.minMagnitudeSpinner);
        maxMagnitudeSpinner = findViewById(R.id.maxMagnitudeSpinner);
        orderBySpinner = findViewById(R.id.orderBy);
        emptyStateText = findViewById(R.id.empty);
        emptyStateImagView = findViewById(R.id.noInternet);

        listView = findViewById(R.id.listView);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);


        //in case user don't select date by default date today
        selectedStartDate = DataProviderFormat.getformateDate(new Date());
        selectedEndDate = DataProviderFormat.getformateDate(new Date());


        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, SearchEarthquakeActivity.this, year, month, day);

        startDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
                start = true;
            }
        });

        endDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
                start = false;
                //selectedEndDate = DatePickerFragment.getDate();

            }
        });


        earthQuakesArrayList = new ArrayList<>();
        earthListAdapter = new EarthQuakeAdapter(this, R.layout.earthquake_item, earthQuakesArrayList);
        listView.setAdapter(earthListAdapter);

        //loadData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SearchEarthquakeActivity.this, Map.class);

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

    }

    private void loadData() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(1, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item1 = item.getItemId();
        if (item1 == R.id.action_search) {
            Log.i(TAG, "onOptionsItemSelected: click");
            invisibleState();
            loadData();
        }

        return super.onOptionsItemSelected(item);
    }

    //Return a new loader instance that is ready to start loading
    @Override
    public Loader<List<EarthQuakes>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: ");


        if (startDate == null || endDate == null || minMagnitudeSpinner == null
                || maxMagnitudeSpinner == null || orderBySpinner == null) {
            Toast.makeText(this, "Please select all fields!", Toast.LENGTH_SHORT).show();
            return null;
        }
        String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + selectedStartDate + "T00:00&endtime=" + selectedEndDate + "T23:59";

        int length = URL.length();

        //Make the url according to to the settings of user
        Uri uri = Uri.parse(URL);
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("minmagnitude", String.valueOf(minMagnitudeSpinner.getSelectedItem()));
        builder.appendQueryParameter("maxmagnitude", String.valueOf(maxMagnitudeSpinner.getSelectedItem()));
        builder.appendQueryParameter("orderby", String.valueOf(orderBySpinner.getSelectedItem()).toLowerCase());
        builder.appendQueryParameter("limit", "200");


        URL = builder.toString();

        //URL.replace('&','?');
        //Url with ? mark not get the result from web
        StringBuilder sb = new StringBuilder(URL);
        sb.setCharAt(length, '&');
        URL = sb.toString();
        Log.i(TAG, "url ready: " + URL);


        Log.i(TAG, "onCreateLoader: going to finish");
        //String url1 = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson&format=geojson&orderby=magnitude-asc";
        return new EarthquakeLoader(this, URL);
    }


    @Override
    public void onLoadFinished(Loader<List<EarthQuakes>> loader, List<EarthQuakes> data) {
        Log.i(TAG, "onLoadFinished: ");


        // Checked the Network State
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkState = connectivityManager.getActiveNetworkInfo();

        //Test if wifi or data connection available or not
        if (networkState == null || !networkState.isConnected()) {
            //Wifi turn off/on
            final WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {

                //set the text and image when no internet
                showState(NO_INTERNET_TEXT, NO_INTERNET_ERROR);
                visibleState();

//                Log.i(TAG, "OFF");
                Snackbar.make(SearchEarthquakeActivity.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Turn on Wifi", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                wifiManager.setWifiEnabled(true);

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
        earthListAdapter.clear();
        earthListAdapter.addAll(data);
        Log.i(TAG, "onLoadFinished: ");
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
    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuakes>> loader) {
        earthListAdapter.clear();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (start) {
            selectedStartDate = year + "-" + month + "-" + dayOfMonth;
            Log.i(TAG, "onCreate: " + selectedStartDate);
        } else {
            selectedEndDate = year + "-" + month + "-" + dayOfMonth;
            Log.i(TAG, "onClick: " + selectedEndDate);
        }
    }

}
