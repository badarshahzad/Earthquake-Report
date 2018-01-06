package com.example.android.earthreport.view.search;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.example.android.earthreport.view.home.HomeFragment;
import com.example.android.earthreport.view.map.Map;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 12/31/17.
 */

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<EarthQuakes>>, DatePickerDialog.OnDateSetListener, PlaceSelectionListener {


    public static final String TAG = SearchFragment.class.getSimpleName();
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

    private PlaceAutocompleteFragment autocompleteFragment;

    private Spinner minMagnitudeSpinner;
    private Spinner maxMagnitudeSpinner;
    private Spinner orderBySpinner;
    private TextView emptyStateText;
    private ImageView emptyStateImagView;
    private Button startDate;
    private Button endDate;
    private ListView listView;
    private List<EarthQuakes> earthQuakesArrayList;
    private EarthQuakeAdapter earthListAdapter;
    private DatePickerDialog datePickerDialog;
    private boolean start = true;
    private String selectedMin;
    private static String locationName;
    private static LatLng latLng;


    public SearchFragment() {
        Log.i(TAG, "SearchFragment: ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int item1 = item.getItemId();
        Log.i(TAG, "onContextItemSelected: +");

        if (item1 == R.id.action_search) {
            Log.i(TAG, "onOptionsItemSelected: click");
            invisibleState();
            loadData();
        }
        if (item1 == R.id.action_filter) {

            //TODO:Add the action filter in the timeline also to get user quick access to the filter

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_search_filter, null);

            minMagnitudeSpinner = view.findViewById(R.id.minOfSpinner);

            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            orderBySpinner = view.findViewById(R.id.orderBy);





            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                    start = true;
                }
            });

            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                    start = false;
                    //selectedEndDate = DatePickerFragment.getTimeStamp();

                }
            });


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

                    //TODO: filter the data exist in the listview
                    selectedMin = String.valueOf(minMagnitudeSpinner.getSelectedItem());
                    Log.i(TAG, "onClick Filter: start date: " + selectedStartDate);
                    Log.i(TAG, "onClick Filter: end date: " + selectedEndDate);
                    Log.i(TAG, "onClick: filter: " + latLng);
                    Log.i(TAG, "onClick: filter: " + locationName);
                    /*******
                     //Hard coded I don't know why this show the month from index 0
                     **/
                    if(selectedStartDate.contains("0")){
                        selectedStartDate.replace("0","1");
                    }

                    if(selectedEndDate.contains("0")){
                        selectedEndDate.replace("0","1");
                    }
                    Toast.makeText(getContext(), latLng + " " + locationName + " " + selectedEndDate + " " + selectedStartDate, Toast.LENGTH_LONG).show();



//                    Toast.makeText(getContext(),"Done",Toast.LENGTH_SHORT).show();

                }
            });

            AlertDialog dialog = addAlert.create();
            dialog.show();
        }


        return super.onContextItemSelected(item);

    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        root = view.findViewById(R.id.root);


      //  minMagnitudeSpinner = view.findViewById(R.id.minMagnitudeSpinner);
      //  maxMagnitudeSpinner = view.findViewById(R.id.maxMagnitudeSpinner);


        //if date is not selected then by default today date
        selectedStartDate = DataProviderFormat.getformateDate(new Date());
        selectedEndDate = DataProviderFormat.getformateDate(new Date());

        emptyStateText = view.findViewById(R.id.empty);
        emptyStateImagView = view.findViewById(R.id.noInternet);


        listView = view.findViewById(R.id.searchListView);

        //in case user don't select date by default date today

        earthQuakesArrayList = new ArrayList<>();
        earthListAdapter = new EarthQuakeAdapter(getActivity(), R.layout.earthquake_item, earthQuakesArrayList);
        listView.setAdapter(earthListAdapter);

        //loadData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), Map.class);

                Bundle bundle = new Bundle();
                bundle.putString(MARK_TYPE, "single");
                bundle.putDouble(LONGITUDE, earthQuakesArrayList.get(position).getLongitude());
                bundle.putDouble(LATITUDE, earthQuakesArrayList.get(position).getLatitude());
                bundle.putString(CITY, earthQuakesArrayList.get(position).getCityname());
                bundle.putString(MAGNITUDE, earthQuakesArrayList.get(position).getMagnitude());
                bundle.putString(DATE, earthQuakesArrayList.get(position).getTimeStamp());


                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setHint("Mianwali, Punjab, Pakistan");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.i(TAG, "onPlaceSelected: place : "+place.getName());
                Log.i(TAG, "onPlaceSelected: latLag: "+place.getLatLng());

            }

            @Override
            public void onError(Status status) {

            }
        });
        //autocompleteFragment.setHint("Mianwali, Pakistan");

        return view;
    }


    /**
     * Title: Override the onActivityResult callback
     * Author: developers.google.com
     * Date: 2018-01-05
     * Code version: N/A
     * Availability: https://developers.google.com/places/android-api/autocomplete
     */


    public void loadData() {
        Log.i(TAG, "loadData: ");

        //. If the loader doesn't already exist, one is created and
        // (if the activity/fragment is currently started) starts the loader.
        // Otherwise the last created loader is re-used.
        getLoaderManager().initLoader(1, null, this);
    }


    //Return a new loader instance that is ready to start loading
    @Override
    public Loader<List<EarthQuakes>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader: ");


        if (startDate == null || endDate == null || minMagnitudeSpinner == null
                || maxMagnitudeSpinner == null || orderBySpinner == null) {
            Toast.makeText(getActivity(), "Please select all fields!", Toast.LENGTH_SHORT).show();
            return null;
        }
        String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + selectedStartDate + "T00:00&endtime=" + selectedEndDate + "T23:59";

        int length = URL.length();

        //Make the url according to to the settings of user
        Uri uri = Uri.parse(URL);
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("minmagnitude", String.valueOf(minMagnitudeSpinner.getSelectedItem()));
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
        return new EarthquakeLoader(getActivity(), URL);
    }


    @Override
    public void onLoadFinished(Loader<List<EarthQuakes>> loader, List<EarthQuakes> data) {
        Log.i(TAG, "onLoadFinished: ");


        // Checked the Network State
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkState = connectivityManager.getActiveNetworkInfo();

        //Test if wifi or data connection available or not
        if (networkState == null || !networkState.isConnected()) {
            //Wifi turn off/on
            final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getActivity().WIFI_SERVICE);

            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {

                //set the text and image when no internet
                showState(NO_INTERNET_TEXT, NO_INTERNET_ERROR);
                visibleState();

//                Log.i(TAG, "OFF");
                Snackbar.make(SearchFragment.root, "No Internet Connection", Snackbar.LENGTH_LONG)
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
    public void invisibleState() {
        emptyStateText.setVisibility(View.INVISIBLE);
        emptyStateImagView.setVisibility(View.INVISIBLE);
    }

    //Visible the text and Icon
    public void visibleState() {
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateImagView.setVisibility(View.VISIBLE);
    }

    public void showState(String text, int img_src) {
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

    @Override
    public void onPlaceSelected(Place place) {
        locationName = String.valueOf(place.getName());
        Log.i(TAG, "Place Name: " + place.getName());
        latLng = place.getLatLng();
        Log.i(TAG, "LatLag:  " + place.getLatLng());
    }

    @Override
    public void onError(Status status) {

    }
}

