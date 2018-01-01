package com.example.android.earthreport.model.sync;

import android.content.Context;
import android.util.Log;

import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.model.utilties.NotificationsUtils;

/**
 * Created by root on 12/25/17.
 */

public class EarthquakeReminderTask {


    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_EARTHQUAKE_REMINDER = "earthquake-occured";
    public static final String TAG = EarthquakeReminderTask.class.getSimpleName();

    public static void executeTask(Context context, String action, EarthQuakes earthQuakesList) {
        //here fetch new data from the urls

        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationsUtils.clearAllNotifications(context);
        }

        if (ACTION_EARTHQUAKE_REMINDER.equals(action)) {
            earthquakeReminder(context, earthQuakesList);
            Log.i(TAG, "executeTask: ");
        }
    }

    private static void earthquakeReminder(Context context, EarthQuakes earthQuakesList) {
        // PreferenceUtilities.setEartquakeList(context);
        NotificationsUtils.remindUser(context, earthQuakesList);
        Log.i(TAG, "earthquakeReminder: ");
    }

}


