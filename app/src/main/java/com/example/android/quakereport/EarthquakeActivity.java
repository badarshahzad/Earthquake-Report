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
package com.example.android.quakereport;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private String TAG = EarthquakeActivity.class.getSimpleName();
    private ListView earthquakeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = findViewById(R.id.list);
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.INTERNET"}, 1);
        new GetEarthquakeData().execute();
    }

    private static class HttpHandler {

        public String TAG = HttpHandler.class.getSimpleName();

        public static String makeServeiceCall(String reqUrl) {
            String response = null;

            try {
                URL url = new URL(reqUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
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
            Toast.makeText(EarthquakeActivity.this, "Earth Quake Data is loading ...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Making a request to url and getting response
            String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-11-12&endtime=2017-11-13&minmag=7";
            String jasonStr = HttpHandler.makeServeiceCall(url);

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

                        //inserting values in the list of earth quakes (model) type and making objects
                        DataProvider.addProduct(mag, place, time);

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

            List<EarthQuakes> values = DataProvider.productList;
            //custom adapter and giving my context, own view to display, values as list to display in List view
            EarthQuakeAdapter earthListAdapter = new EarthQuakeAdapter(EarthquakeActivity.this, R.layout.earthquake_item, values);


            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(earthListAdapter);

        }

    }
}
