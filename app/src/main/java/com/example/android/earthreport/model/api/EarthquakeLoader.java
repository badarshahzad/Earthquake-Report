package com.example.android.earthreport.model.api;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.model.utilties.ParseUSGSJsonUtils;

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


    public static String TAG = EarthquakeLoader.class.getSimpleName();
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
        earthQuakesArrayList = new ParseUSGSJsonUtils().parseJsonIntoData(earthQuakesArrayList, jasonStr, getContext());
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