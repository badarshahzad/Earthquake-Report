package com.example.android.earthreport.model.utilties;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.earthreport.model.pojos.EarthQuakes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by root on 1/4/18.
 */

public class ParseUSGSJsonUtils {

    public static final String TAG = ParseUSGSJsonUtils.class.getSimpleName();

    public ParseUSGSJsonUtils(){

    }

    public List<EarthQuakes> parseJsonIntoData(List<EarthQuakes> earthQuakesArrayList, String jasonStr, Context context) {

        //if the internet available and the jason data receive in jasonStr then
        if (jasonStr != null) {

            //Created a dumivalues list of earthquake magnitude, cityname and date

            try {

                //get json string values as an object
                JSONObject root = new JSONObject(jasonStr);
                //get the json arrray from jason object
                final JSONArray features = root.getJSONArray("features");

                for (int a = 0; a < features.length(); a++) {
                    //get the indexes values of objects from features array
                    JSONObject indexes = features.getJSONObject(a);
                    //in each index get the value from the wrap object
                    JSONObject properties = indexes.getJSONObject("properties");

                    //first value magnitude get from properties object
                    String mag = properties.getString("mag");
                    //second value location get fro the properties object
                    String place = properties.getString("place");
                    //third value date and time get form the properties object
                    String time = properties.getString("time");
                    //fourth value url if user want to detail
                    String url = properties.getString("url");

                    JSONObject geometry = indexes.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    /***
                     * I test on 5 different devices but on QMobile Android version 4.3 this error occure
                     * double longitude = (double) coordinates.get(0);
                     * Honestly just one device show an error don't cast Integer while other didn't give error?
                     *So, i simply with concatenate a string with interger and parse it into double :)
                     * **/
                    double longitude = Double.valueOf(coordinates.get(0) + "");
                    double latitude = Double.valueOf(coordinates.get(1) + "");

//                    Log.i(TAG, "loadInBackground: " + longitude + " " + latitude);

                    //inserting values in the list of earth quakes (model) type and making objects
                    // TimelineFragment.earthQuakesArrayList.add(new EarthQuakes(mag, place, time, url, longitude, latitude));

                    //SharefPreferences filter Setting
                    /**
                     * Remember always add the file name where the preference is store
                     * You can find the file name project path -> SheredPrefs -> preferences
                     * */
                 //   Log.i(TAG, "getsharedPreferenced: "+context.getSharedPreferences("com.example.android.earthreport_preferences.xml",Context.MODE_PRIVATE));
                 //   Log.i(TAG, "getDefaultSheredPreference: "+ PreferenceManager.getDefaultSharedPreferences(context));
//                    Log.i(TAG, "package name: "+context.getPackageName());
                    if(context==null){
                        Log.i(TAG, "context null");
                    }

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String filterMag = sharedPref.getString("key_filter_magnitude", "5");
                    Log.d(TAG, "filterValue: " + filterMag);

                    //in case user want all magnitudes
                    if (filterMag.equalsIgnoreCase("All")) {
                        filterMag = "0";
                    }

                    //Incase the setting filter is null (08:30 am 2017-01-03)
                    //if(filterMag==null)

                    //filter the arraylist while data fetching and passing into arraylist
                    // if the data palce is not fetch or mention


                    Log.i(TAG, "parseJsonIntoData: mag: "+mag +" filer mag: "+ filterMag);
                    //TODO:application stop due to 107 invalid double null
                    if (place != null && mag != null && filterMag != null &&
                            place.contains("of") && Double.valueOf(mag) >= Double.valueOf(filterMag)) {
                        earthQuakesArrayList.add(new EarthQuakes(mag, place, time, url, longitude, latitude));
                        //    Log.i(TAG, "Yes contains of");
                    }

//                    Log.i(TAG, "loadInBackground: size " + earthQuakesArrayList.size());
//                    Log.i(TAG, "data added " + mag + " " + place + " " + time);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //if the jason string is null that could be the case when internet is no available
        }
        return earthQuakesArrayList;
    }
}
