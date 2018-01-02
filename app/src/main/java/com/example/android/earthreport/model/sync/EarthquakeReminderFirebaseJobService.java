package com.example.android.earthreport.model.sync;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.earthreport.model.api.EarthquakeLoader;
import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.model.utilties.PreferenceUtilities;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/25/17.
 */

public class EarthquakeReminderFirebaseJobService extends JobService {

    public static final String TAG = EarthquakeReminderFirebaseJobService.class.getSimpleName();
    public List<EarthQuakes> earthQuakesList;
    Context context = EarthquakeReminderFirebaseJobService.this;
    private String HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
    private AsyncTask backgroundTask;
    private int rememberIndex = 0;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {


        backgroundTask = new AsyncTask() {
            //main thread
            @Override
            protected Object doInBackground(Object[] objects) {
                earthQuakesList = new ArrayList<>();
                Context context = EarthquakeReminderFirebaseJobService.this;
                String jsonResponse = EarthquakeLoader.HttpHandler.makeServeiceCall(HOUR_URL);
                earthQuakesList = EarthquakeLoader.parseJsonIntoData(earthQuakesList, jsonResponse, context);

                List<EarthQuakes> checkList = new ArrayList<>();

                //TODO:Check if already earhquake exist then don't give notification
                SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(context);
                String json = shPref.getString(PreferenceUtilities.KEY_EARTHQUAKES, "null");


                if (json.equals("null")) {
                    Log.i(TAG, "doInBackground: string null");

                    checkList = EarthquakeLoader.parseJsonIntoData(checkList, json, context);
                    Log.i(TAG, "" + checkList);
                    //  Log.i(TAG, "doInBackground: "+checkList.get(0));
                    //  Log.i(TAG, "doInBackground: "+checkList.get(0).getCityname());

                }


                for (int a = 0; a < checkList.size(); ) {
                    if (checkList.get(a).getCityname() == earthQuakesList.get(a).getCityname()
                            && checkList.get(a).getMagnitude() == earthQuakesList.get(a).getMagnitude()) {
                        a++;
                    } else {
                        rememberIndex = a;

                      /*
                        if(rememberIndex>5){
                            rememberIndex = 0;
                        }*/
                    }
                    Log.i(TAG, "doInBackground: already exist");
                    return null;
                }

                //this is incase the index exceed and in new hour
                //start then index could be 3 to 20+ but the in hour
                //there is possiblity no earthquake so
                if (earthQuakesList.size() == 0) {
                    return null;
                }

                EarthQuakes earthQuakes = earthQuakesList.get(rememberIndex);

                //Log.i(TAG, "doInBackground: "+earthQuakesList.get(0).getCityname());
                EarthquakeReminderTask.executeTask(context, EarthquakeReminderTask.ACTION_EARTHQUAKE_REMINDER, earthQuakes);
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

        //when downloading somtheing and wifi gone then we have to stop the thread
        // Anna wa chor ta ne dawnra na wat ! :D
        if (backgroundTask != null) backgroundTask.cancel(true);

        //true mean the as soon as the condition re-met the job should be retery again;
        return true;
    }
}
