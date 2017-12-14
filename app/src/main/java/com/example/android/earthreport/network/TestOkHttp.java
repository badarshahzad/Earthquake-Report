package com.example.android.earthreport.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 12/10/17.
 */

public class TestOkHttp {

    public static OkHttpClient client = new OkHttpClient();

    //-----------OkHttp library test
    public static String run(String url) throws IOException {


        Request request = new Request
                .Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        Log.i("OKHTTP", "data : " + response.body().string());
        // Toast.makeText(EarthquakeActivity.this,"Data Fetch:"+response.body().string(),Toast.LENGTH_LONG).show();
        return response.body().string();
    }

}
