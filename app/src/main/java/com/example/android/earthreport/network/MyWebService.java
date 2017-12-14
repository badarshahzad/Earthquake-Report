package com.example.android.earthreport.network;

import com.example.android.earthreport.model.EarthQuakes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by root on 12/10/17.
 */

public interface MyWebService {

    String BASE_URL = "https://earthquake.usgs.gov/";
    String FEED = "earthquakes/feed/v1.0/summary/all_day.geojson";
//    String FEED = "fdsnws/event/1/count?format=geojson&starttime=2017-12-10";

    Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET(FEED)
    Call<List<EarthQuakes>> earquakesCount();
}
