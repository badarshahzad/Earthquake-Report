package com.example.android.earthreport.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.earthreport.EarthQuakeAdapter;
import com.example.android.earthreport.R;
import com.example.android.earthreport.main.EarthquakeActivity;
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
public class GetEarthquakeData extends AsyncTask<String, Void, Void> {


    public String TAG = GetEarthquakeData.class.getSimpleName();
    //what is List and ArrayList difference? List<EarthQuakes> earthQuakesList;
    ArrayList<EarthQuakes> earthQuakesList;
    private Context context;
    private ListView earthquakeListView;

    // private String todayEarthquakesCountUrl = "https://earthquake.usgs.gov/fdsnws/event/1/count?starttime=2017-12-10";
    private int count;

    //private EarthQuakeAdapter earthListAdapter;

    public GetEarthquakeData() {

    }

    public GetEarthquakeData(Context context, ListView earthquakeListView, ArrayList<EarthQuakes> earthQuakesList) {
        this.context = context;
        this.earthquakeListView = earthquakeListView;
//        earthQuakesList = new ArrayList<>();
        this.earthQuakesList = earthQuakesList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //Give message to user the data is downloading
        // Toast.makeText(context,
        //         "Earth Quake Data is loading ...",
        //         Toast.LENGTH_SHORT).show();


        //Wifi turn off/on
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            Log.i("WIFI", "ON");
            Snackbar.make(EarthquakeActivity.root, "Hello", Snackbar.LENGTH_LONG)
                    .setAction("Turn on Wifi", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wifiManager.setWifiEnabled(true);
                            Toast.makeText(context, "WIFI Enabled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }

        //	check the internet conneccctivity
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Test if wifi is disable turn on with snakbar click lister
        if (networkInfo == null || !networkInfo.isConnected()) {

            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            //to cancel the doingbackgroudn return and onCancel you don't go on post executre
            //you don't go into do in background you just return back
            cancel(true);
        }
    }


    @Override
    protected Void doInBackground(String... voids) {
        //Making a request to url and getting response

        String jasonStr = HttpHandler.makeServeiceCall(voids[0]);

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

                    //I test on 5 different devices but on QMobile Android version 4.3 this error occure
                    //double longitude = (double) coordinates.get(0);
                    //Honestly just one device show an error don't cast Integer while other didn't give error?
                    //So, i simply with concatenate a string with interger and parse it into double :)
                    double longitude = Double.valueOf(coordinates.get(0) + "");
                    double latitude = Double.valueOf(coordinates.get(1) + "");

                    //inserting values in the list of earth quakes (model) type and making objects
                    earthQuakesList.add(new EarthQuakes(mag, place, time, url, longitude, latitude));
                    //DataProvider.addProduct(mag, place, time, url, longitude, latitude);
                    Log.i(TAG, "data added " + mag + " " + place + " " + time);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //if the jason string is null that could be the case when internet is no available
        } else {
            Log.e(TAG, "Couldn't get jason from server.");

            //I get the idea of runnign below thread from Sir Sarmad example
            ((Activity) context).runOnUiThread(new Runnable() {
                //runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Couldn't get jason from server. Check your network connection!", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Couldn't get jason from server. Check your network connection!", Toast.LENGTH_LONG).show();
                }
            });
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //List<EarthQuakes> values = DataProvider.valuesList;
        //custom adapter and giving my context, own view to display, values as list to display in List view
        EarthQuakeAdapter earthListAdapter = new EarthQuakeAdapter(context, R.layout.earthquake_item, earthQuakesList);

        //TODO: Show progressbar

        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(earthListAdapter);

    }


    public static class HttpHandler {

        public String TAG = HttpHandler.class.getSimpleName();

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