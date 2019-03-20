package com.badarshahzad.android.earthreport.model.sync;

import android.content.Context;
import android.util.Log;

import com.badarshahzad.android.earthreport.model.pojos.EarthQuakes;
import com.badarshahzad.android.earthreport.model.utilties.NotificationsUtils;

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

public class EarthquakeReminderTask {


    /**
     * I learnt the Firebase job dispacther task after making
     * when charging and water reminder task on udacity;
     */
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_EARTHQUAKE_REMINDER = "earthquake-occured";
    public static final String TAG = EarthquakeReminderTask.class.getSimpleName();

    public static void executeTask(Context context, String action, EarthQuakes newEarthQuake) {

        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationsUtils.clearAllNotifications(context);
        }
        if (ACTION_EARTHQUAKE_REMINDER.equals(action)) {
            earthquakeReminder(context, newEarthQuake);
            Log.i(TAG, "executeTask: ");
        }
    }

    private static void earthquakeReminder(Context context, EarthQuakes newEarthQuake) {
        // PreferenceUtils.setEartquakeList(context);
        NotificationsUtils.remindUser(context, newEarthQuake);
        Log.i(TAG, "earthquakeReminder: ");
    }


}


