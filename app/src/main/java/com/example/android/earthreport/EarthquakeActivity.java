/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.earthreport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {


    private static final String PREF_FILE = "com.example.android.earthreport.preferences";
    private static final String KEY_QUAKEVALUES = "KEY_QUAKEVALUES";

    private static List<EarthQuakes> values;
    private String TAG = EarthquakeActivity.class.getSimpleName();
    private ListView earthquakeListView;
    private EarthQuakeAdapter earthListAdapter;
    private ConstraintLayout constraintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        constraintView = findViewById(R.id.constraint);
        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = findViewById(R.id.list);

        // here we can give the argument in execute the argument could be the `url`
        //to get data from web
        new GetEarthquakeData().execute();

        //Add list view listener to open detail activity of each list view value
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Log.i("URL: ",values.get(position).getUrl());
                //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(values.get(position).getUrl()));
                Intent intent = new Intent(EarthquakeActivity.this, Map.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("LONGITUDE", values.get(position).getLongitude());
                bundle.putDouble("LATITUDE", values.get(position).getLatitude());
                bundle.putString("CITY", values.get(position).getCityname());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }


    private void showSnackBarMessage() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Stop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
    }


    private static class HttpHandler {

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

    //This url handling and getting data learn from www.tutorialspoint.com/android/android_json_parser.htm
    private class GetEarthquakeData extends AsyncTask<Void, Void, Void> {


        public String TAG = GetEarthquakeData.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Give message to user the data is downloading
            Toast.makeText(EarthquakeActivity.this,
                    "Earth Quake Data is loading ...",
                    Toast.LENGTH_SHORT).show();
            //	check the internet conneccctivity
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Snackbar.make(constraintView, "Connection Error", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();

                cancel(true);
            }
//checc the response code
//if(netowkrik = =null || !netowkriinfo.isConnected){
//set text no connection error cancel (true); to cancel the doingbackgroudn return and onCancel you don't go on post executre
//you don't go into do in background you just return back
//}

        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Making a request to url and getting response
            String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-11-10&endtime=2017-11-14&minmag=1&maxmag=10";
            //String URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";
            String jasonStr = HttpHandler.makeServeiceCall(URL);

            //if the internet available and the jason data receive in jasonStr then
            if (jasonStr != null) {

                //Created a dumivalues list of earthquake magnitude, cityname and date

                try {
                    //get json string values as an object
                    JSONObject root = new JSONObject(jasonStr);
                    //get the json arrray from jason object
                    JSONArray features = root.getJSONArray("features");

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

                        double longitude = (double) coordinates.get(0);
                        double latitude = (double) coordinates.get(1);


                        //inserting values in the list of earth quakes (model) type and making objects
                        DataProvider.addProduct(mag, place, time, url, longitude, latitude);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //if the jason string is null that could be the case when internet is no available
            } else {
                Log.e(TAG, "Couldn't get jason from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get jason from server. Check your network connection!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Snackbar.make(constraintView, "Data Loaded", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
            List<EarthQuakes> values = DataProvider.productList;
            //custom adapter and giving my context, own view to display, values as list to display in List view
            earthListAdapter = new EarthQuakeAdapter(EarthquakeActivity.this, R.layout.earthquake_item, values);

            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(earthListAdapter);

        }

    }

}
