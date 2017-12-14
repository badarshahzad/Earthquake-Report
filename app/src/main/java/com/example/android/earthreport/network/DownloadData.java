package com.example.android.earthreport.network;

/**
 * Created by root on 12/13/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.model.EarthQuakes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by root on 11/24/17.
 */
//This url handling and getting data learn from www.tutorialspoint.com/android/android_json_parser.htm
public class DownloadData extends AsyncTask<String, Void, Void> {


    public static String TAG = DownloadData.class.getSimpleName();

    //TODO: what is List and ArrayList difference? List<EarthQuakes> earthQuakesList;

    ArrayList<ArrayList> arrayLists;
//    private int count;

    public DownloadData() {

    }

    public DownloadData(ArrayList<ArrayList> earthQuakesList) {

        this.arrayLists = earthQuakesList;
    }

    @Override
    protected Void doInBackground(String... voids) {
        //Making a request to url and getting response
        //String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-11-10&endtime=2017-11-14&minmag=1&maxmag=10";
        //String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-11-10&endtime=2017-11-14&minmag=1&maxmag=10";

        //one hour
        // String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

        //one day
        //String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

        //String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";


        for (int count = 0; count < voids.length; count++) {
            //pick the urls one by one and fetch jsdon data

            String jasonStr = com.example.android.earthreport.network.GetEarthquakeData.HttpHandler.makeServeiceCall(voids[count]);
            //if the internet available and the jason data receive in jasonStr then
            if (jasonStr != null) {

                //Created a dumivalues list of earthquake magnitude, cityname and date

                try {

                    //get json string values as an object
                    JSONObject root = new JSONObject(jasonStr);
                    //get the json arrray from jason object
                    final JSONArray features = root.getJSONArray("features");

                    Log.i("COUNT", features.length() + "");

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

                        //I test on 5 different devices but on QMobile Android version 4.3 this error occure
                        //double longitude = (double) coordinates.get(0);
                        //Honestly just one device show an error don't cast Integer while other didn't give error?
                        //So, i simply with concatenate a string with interger and parse it into double :)
                        double longitude = Double.valueOf(coordinates.get(0) + "");
                        double latitude = Double.valueOf(coordinates.get(1) + "");


                        //inserting values in the list of earth quakes (model) type and making objects
                        DataProvider.arrayLists.get(count).add(new EarthQuakes(mag, place, time, url, longitude, latitude));
                        Log.i(TAG, "data added " + mag + " " + place + " " + time);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //if the jason string is null that could be the case when internet is no available
            } else {
          /*  Log.e(TAG, "Couldn't get jason from server.");

            //I get the idea of runnign below thread from Sir Sarmad example
            ((Activity) context).runOnUiThread(new Runnable() {
                //runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Couldn't get jason from server. Check your network connection!", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Couldn't get jason from server. Check your network connection!", Toast.LENGTH_LONG).show();
                }
            });*/
            }

        }


        return null;
    }

    public static class HttpHandler {

        public String TAG = com.example.android.earthreport.network.GetEarthquakeData.HttpHandler.class.getSimpleName();

        public static String makeServeiceCall(String reqUrl) {
            String response = null;

            try {
                URL url = new URL(reqUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
//connection.setConnectTimeout(500);
//check the method
//connectionn timeout
//check the status is HTTTP.ok or not
                //read the response
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                response = converStreamToString(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        private static String converStreamToString(InputStream inputStream) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String stringLine;
            try {
                while ((stringLine = reader.readLine()) != null) {
                    sb.append(stringLine).append('\n');
                    Log.i("Append Data: ", stringLine);
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
