package com.example.android.earthreport.model.api;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.earthreport.model.pojos.EarthQuakes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 12/16/17.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuakes>> {


    public String TAG = EarthquakeLoader.class.getSimpleName();
    private String URL = null;
    private Context context;
    private List<EarthQuakes> earthQuakesArrayList;

    public EarthquakeLoader(Context context, String URL) {

        super(context);
        this.context = context;
        this.URL = URL;
        earthQuakesArrayList = new ArrayList<>();
//        Log.d(TAG, "EarthquakeLoaderConstructor: ");
    }

    public static List<EarthQuakes> parseJsonIntoData(List<EarthQuakes> earthQuakesArrayList, String jasonStr, Context context) {

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
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    String filterMag = sharedPref.getString("key_filter_magnitude", "5");
//                    Log.d(TAG, "filterValue: " + filterMag);

                    //in case user want all magnitudes
                    if (filterMag.equalsIgnoreCase("All")) {
                        filterMag = "0";
                    }

                    //filter the arraylist while data fetching and passing into arraylist
                    // if the data palce is not fetch or mention

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

    @Override
    protected void onStartLoading() {
//        Log.d(TAG, "onStartLoading: ");
        forceLoad();
    }

    @Override
    public List<EarthQuakes> loadInBackground() {

        //Making a request to url and getting response
//        Log.d(TAG, "loadInBackground: ");
        String jasonStr = HttpHandler.makeServeiceCall(URL);
        earthQuakesArrayList = parseJsonIntoData(earthQuakesArrayList, jasonStr, getContext());
//        Log.d(TAG, "loadInBackground: finished ");
        return earthQuakesArrayList;
    }

    public static class HttpHandler {

        public static String TAG = HttpHandler.class.getSimpleName();

        public static String makeServeiceCall(String reqUrl) {

            String response = null;
            HttpURLConnection connection = null;
            //   InputStream inputStream = null;
            URL url = null;

            try {

                url = new URL(reqUrl);

                //Return a URLConnection instance that represents connection to the remote
                //object referret to b the URL
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();

//                Log.i(TAG, "makeServeiceCall Response code: " + connection.getResponseCode());

                //if the request is successfull
                if (connection.getResponseCode() == 200) {

                    //read the response
                    //inputStream = new BufferedInputStream(connection.getInputStream());

                    //readFrom the stream that contains the result
                    response = converStreamToString(connection.getInputStream());

                }


            } catch (MalformedURLException e) {
                //TODO: Show the notifications in case no response
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
//                Log.i(TAG, "makeServeiceCall: exception");
            } finally {

                //disconnect the connection
                if (connection != null) {
                    connection.disconnect();

//                    Log.i(TAG, "makeServeiceCall: connection close");
                }

            }

//            Log.i(TAG, "makeServeiceCall: response back");
            return response;
        }

        //Convert the inputstream raw data into human readable string  which
        //contains json response from the server
        private static String converStreamToString(InputStream inputStream) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            String stringLine;

            try {
                while ((stringLine = reader.readLine()) != null) {
                    sb.append(stringLine);
                    //sb.append(stringLine).append('\n');
                    //   Log.i("Append Data: ", stringLine);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }

    }

}