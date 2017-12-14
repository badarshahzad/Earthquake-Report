package com.example.android.earthreport.network;

import android.os.Bundle;
import android.os.Message;

import com.example.android.earthreport.fragments.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 12/11/17.
 */

public class GetEarthquakeCount {

    private static final String TAG = GetEarthquakeCount.class.getSimpleName();

    // HomeFragment homeFragment = new HomeFragment();
    public GetEarthquakeCount() {

    }

    public void dataFetch(final String[] URLS) {

        //start the progressbar and visible


        final int[] count = new int[3];
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (int a = 0; a < URLS.length; a++) {

                    String jasonStr = GetEarthquakeData.HttpHandler.makeServeiceCall(URLS[a]);
                    //if the internet available and the jason data receive in jasonStr then
                    if (jasonStr != null) {

                        //get json string values as an object
                        JSONObject root = null;

                        try {
                            root = new JSONObject(jasonStr);
                            count[a] = root.getInt("count");
                            // Log.i("COUNT", count[0] + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(HomeFragment.TODAY_COUNT_KEY, String.valueOf(count[0]));
                bundle.putString(HomeFragment.WEEK_COUNT_KEY, String.valueOf(count[1]));
                bundle.putString(HomeFragment.MONTH_COUNT_KEY, String.valueOf(count[2]));
                message.setData(bundle);
                HomeFragment.handler.sendMessage(message);
            }
        };

        Thread th = new Thread(runnable);
        th.start();
    }

}
