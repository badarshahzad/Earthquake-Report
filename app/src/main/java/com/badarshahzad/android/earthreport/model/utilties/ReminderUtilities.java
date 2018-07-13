package com.example.android.earthreport.model.utilties;

import android.content.Context;

import com.example.android.earthreport.model.sync.EarthquakeReminderFirebaseJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

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

public class ReminderUtilities {


    public static final String TAG = ReminderUtilities.class.getSimpleName();
    private static final String REMINDER_JOB_TAG = "earthquake_occured";
    private static boolean sIntialized;

    //never excute more than once at a time
    synchronized public static void scheduleEarthquakeReminder(final Context context) {

        //if it is already initilized then
        if (sIntialized) return;

        // Log.i(TAG, "scheduleEarthquakeReminder: "+REMINDER_INTERVAL_SECONDS);

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(EarthquakeReminderFirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_IDLE)
                .setLifetime(Lifetime.FOREVER) //if reboot
                .setRecurring(true) //occure periodically
                .setTrigger(Trigger.executionWindow(60, 60+60))
                .setReplaceCurrent(true) //if the job ever remade replace job with the new one
                .build();
        dispatcher.schedule(constraintReminderJob);
        sIntialized = true;

    }

}
