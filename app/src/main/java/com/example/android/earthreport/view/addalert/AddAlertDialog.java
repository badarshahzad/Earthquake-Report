package com.example.android.earthreport.view.addalert;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.earthreport.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by root on 12/23/17.
 */

public class AddAlertDialog  extends DialogFragment {
    private static String TAG = AddAlertDialog.class.getSimpleName();


    //TODO: Convert this filter class to search class


    private String selectedMin;

    //add google search locaiton
    private String selectedCountery;

    private Spinner minMagnitudeSpinner;
    private Spinner regionSpinner;

    View.OnClickListener donefilterAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();

            selectedMin = String.valueOf(minMagnitudeSpinner.getSelectedItem());
            selectedCountery = String.valueOf(regionSpinner.getSelectedItem());

//            TimelineFragment timelineFragment = new TimelineFragment();
//            timelineFragment.filterRefreshList(selectedPeriod, selectedMin, selectedMax, selectedCountery);

            getDialog().dismiss();
        }
    };

    public AddAlertDialog(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_alert, container, false);
        getDialog().setTitle("Filter Earthquakes");

        Button donefilter = view.findViewById(R.id.done_filter);
        donefilter.setOnClickListener(donefilterAction);

        minMagnitudeSpinner = view.findViewById(R.id.min_of);
        regionSpinner = view.findViewById(R.id.region_of);


         if(isCounteryExist(getContext(), -4.3777,101.9364)){
           Log.i(TAG, "onCreate: Now you are ready!");
        }
        if(isCounteryExist(getContext(), 36.9294,71.3741)){
            Log.i(TAG, "onCreate: Now you are ready!");
        }
        if(isCounteryExist(getContext(), -5.4215,-80.4563)){
            Log.i(TAG, "onCreate: Now you are ready!");
        }

        List<String> countries = getCountriesName();
        //sort the countries name
        Collections.sort(countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                countries
        );

        regionSpinner.setAdapter(adapter);

        return view;
    }

    public ArrayList<String> getCountriesName() {
        ArrayList<String> list = new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            //Log.i(TAG, "Country Name = " + obj.getDisplayCountry());
            list.add(obj.getDisplayCountry());

        }
        return list;
    }

    public static boolean isCounteryExist(Context context, double latitude, double longitude){
        String counteryName = null;
        try {
            counteryName = getCountryName(context,latitude,longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> list=new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            //Log.i(TAG, "Country Name = " + obj.getDisplayCountry());
            list.add(obj.getDisplayCountry());


            if(counteryName!=null && counteryName.equals(obj.getDisplayCountry())){
                Log.i(TAG, "Get Display Countery"+obj.getDisplayCountry());
                Log.i(TAG, "Countery Name: "+obj.getDisplayCountry());
                return true;
            }
        }
        return false;
    }

    public static String getCountryName(Context context, double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if(addresses.isEmpty()){return null; }

        //Log.i(TAG, "getCountryName:1 "+addresses.get(0).getCountryName());
        Address result;

        if (addresses != null && !addresses.isEmpty()) {
            //  Log.i(TAG, "getCountryName:2 "+addresses.get(0).getCountryName());
            return addresses.get(0).getCountryName();
        }
        return null;
    }

}
