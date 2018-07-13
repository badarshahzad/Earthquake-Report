package com.badarshahzad.android.earthreport.model.sync;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.badarshahzad.android.earthreport.model.api.EarthquakeLoader;
import com.badarshahzad.android.earthreport.model.pojos.EarthQuakes;
import com.badarshahzad.android.earthreport.model.utilties.ParseUSGSJsonUtils;
import com.badarshahzad.android.earthreport.model.utilties.PreferenceUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/25/17.
 */

/**
 * Title: Background Tasks
 * Author: classroom.udacity.com
 * Date: 2017-12-25
 * Code version: N/A
 * Availability: https://classroom.udacity.com/courses/ud851/lessons/f5ef4e52-c485-4c85-
 * a26a-3231c17d6154/concepts/3ac594fa-2597-4956-ae0e-99706ceb206e
 */


public class EarthquakeReminderFirebaseJobService extends JobService {

    public static final String TAG = EarthquakeReminderFirebaseJobService.class.getSimpleName();
    private String HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    private AsyncTask backgroundTask;
    private int rememberIndex = 0;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {


        backgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                List<EarthQuakes> earthQuakesList = new ArrayList<>();
                Context context = EarthquakeReminderFirebaseJobService.this;
                //get the data of hour from the USGS website
                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(HOUR_URL);

                Log.i(TAG, "doInBackground: response "+jsonResponse);
                //if the response from the network null
                if(jsonResponse == null){
                    return  null;
                }

                //make object to access the method for parsing

                //this method take objects list, json response consist string and context and return list
                earthQuakesList = new ParseUSGSJsonUtils().parseJsonIntoData(earthQuakesList, jsonResponse, context);
                Log.i(TAG, "doInBackground: size of list: "+earthQuakesList.size());

                /**
                 * This is in case the index exceed and in new hour start then index could be
                 * 3 to 20+ or even 0 but the in hour there is possibility no earthquake so
                 * */
                if (earthQuakesList.size() == 0) {
                    Log.i(TAG, "doInBackground: may be filter trouble");
                    return null;
                }

                //List<EarthQuakes> checkList = new ArrayList<>();

                //TODO:Check if already earhquake exist then don't give notification
                SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(context);
                String json = shPref.getString(PreferenceUtils.KEY_EARTHQUAKES, "null");

                //get the first index of hour earthquake
                EarthQuakes newEarthquake = earthQuakesList.get(0);

                if (json.equals("null")) {
                    Log.i(TAG, "doInBackground: string null");

                    /**if the the results is null in preferances then add the List as a string
                     * for this purpose Gson class help me to convert my object into gson and then toString
                     * help me to store the string in preferances
                     */
                    //EarthQuakes earthquake = new Gson().fromJson(earthQuakesListString, EarthQuakes.class);

                    //list as string store after converting list->json->string
                    shPref.edit().remove(PreferenceUtils.KEY_EARTHQUAKES).putString(PreferenceUtils.KEY_EARTHQUAKES,new Gson().toJson(newEarthquake).toString()).apply();
                    EarthquakeReminderTask.executeTask(context, EarthquakeReminderTask.ACTION_EARTHQUAKE_REMINDER, newEarthquake);

                    Log.i(TAG, "Store the list: " + new Gson().toJson(earthQuakesList.get(0)).toString());
                    //  Log.i(TAG, "doInBackground: "+checkList.get(0));
                    //  Log.i(TAG, "doInBackground: "+checkList.get(0).getCityname());

                }else {

                    json = shPref.getString(PreferenceUtils.KEY_EARTHQUAKES, "null");

                    //fromJson  deserializes the specified Json into an object of the specified class.
                    EarthQuakes  storedEarthquake = new Gson().fromJson(json, EarthQuakes.class);

                   // EarthQuakes previousEarthQuake =  new Gson().fromJson(earthQuakesCompareList.get(0));

                    //Showing the to list as json to watch on log
                    Log.i(TAG, " new hour list:  "+ new Gson().toJson(storedEarthquake).toString());
                    Log.i(TAG, " new hour list:  "+ new Gson().toJson(newEarthquake).toString());


                    /**
                     * The below process is hardcoded as sometime the logic not work due to
                     * dynamic work. Yes there could be just one Model class to check except
                     * one index but in future work I hope I will add more intereting thing with
                     * hour data as a list. For this time just index 0. :)
                     */


                    /**
                     * The earthQuakesList is new fetch data
                     */

                    //new fetch date list 0 index
                    String cityName = newEarthquake.getCityname();
                    String timeStamp = newEarthquake.getTimeStamp();
                    Log.i(TAG, "doInBackground: new "+ cityName+"  "+timeStamp);

                    /**
                     * The earthQuakesCompare is store data (that I store first time or
                     * after match not found to give notification and store new value)
                     */

                    //stored list first index
                    String storeCityName = storedEarthquake.getCityname();
                    String storeTimeStamp = storedEarthquake.getTimeStamp();
                    Log.i(TAG, "doInBackground: store "+ storeCityName+"  "+storeTimeStamp);

                    //check if city and timestamp consists then don't give notification otherwise vice versa :)
                    //the condition is when the city and time stamp don't match then give notification.
                    if(!(cityName.equals(storeCityName) && timeStamp.equals(storeTimeStamp))) {

                        Log.i(TAG, "doInBackground: towards notificaiton moving");

                        shPref.edit().remove(PreferenceUtils.KEY_EARTHQUAKES).putString(PreferenceUtils.KEY_EARTHQUAKES, new Gson().toJson(newEarthquake).toString()).apply();
                        String getSoreValue = shPref.getString(PreferenceUtils.KEY_EARTHQUAKES, "null");

                        Log.i(TAG, "doInBackground: store retrieve : "+getSoreValue);
                        //fromJson  deserializes the specified Json into an object of the specified class.
                        EarthQuakes  s = new Gson().fromJson(getSoreValue, EarthQuakes.class);
                        Log.i(TAG, "doInBackground: store retrieve : "+s);


                        //Log.i(TAG, "doInBackground: "+earthQuakesList.get(0).getCityname());
                        EarthquakeReminderTask.executeTask(context, EarthquakeReminderTask.ACTION_EARTHQUAKE_REMINDER, newEarthquake);

                    }

                }

                return null;
            }

            //when job finished


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //job finished so no need to reschedule the job so false
                jobFinished(jobParameters, false);
            }
        };
        backgroundTask.execute();
        //job finished so no need to reschedule the job so false
        //return true as the job is still doing some work
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        //We have to stop the service e.g
        //when downloading somtheing and wifi gone then we have to stop the thread
        if (backgroundTask != null) backgroundTask.cancel(true);

        //true mean the as soon as the condition re-met the job should be retery again;
        return true;
    }
}
