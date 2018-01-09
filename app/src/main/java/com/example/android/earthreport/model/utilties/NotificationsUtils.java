package com.example.android.earthreport.model.utilties;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.example.android.earthreport.controller.map.Map;
import com.example.android.earthreport.controller.timeline.TimelineFragment;
import com.google.gson.Gson;

/**
 * Created by root on 12/17/17.
 */

public class NotificationsUtils {

    public static final String TAG = NotificationsUtils.class.getSimpleName();
    public static EarthQuakes earthQuakes;
    /**
     * Title: Notifications
     * Author: classroom.udacity.com
     * Date: 2017/12/17
     * Code version: N/A
     * Availability: https://classroom.udacity.com/courses/ud851/lessons/f5ef4e52-c485-4c85-a26a-3231c17d6154/concepts/dd7d98f2-decb-42f0-95c2-7f0da074249d
     */

    //this notificaiton id help me to access  our notification after displayed it.
    //This will be handy when I need to cancel the notificaiton
    private static int NEW_EARTHQUAKE_NOTIFICATION_ID = 1110;
    private static String NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID = "earthquake_notification_channel";

    public static void remindUser(Context context, EarthQuakes newEarthQuakes) {

        if (newEarthQuakes != null) {
            earthQuakes = newEarthQuakes;
        }

        //get the notification service from notification manager from context.getSystemService
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //to check the setting setting user enabled notification and vibrate or not
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //create a notification channel for Android devices
        //NotificaitonChannel: [A representation of settings that apply to a collection
        // of similarly themed notifications.]
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationChannel = new NotificationChannel(
                    NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID,context.getString(R.string.main_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH);

            // To check the Vibrate and Notification turn ON/OFF
            if(sharedPreferences.getBoolean("key_vibrate",true)) {
                notificationChannel.enableVibration(true);
            }else{
                notificationChannel.enableVibration(false);
            }

            notificationManager.createNotificationChannel(notificationChannel);

        }

        String earthQuakesListString = sharedPreferences.getString(PreferenceUtils.KEY_EARTHQUAKES, null);
        //fromJson  deserializes the specified Json into an object of the specified class.
        EarthQuakes earthquake = new Gson().fromJson(earthQuakesListString, EarthQuakes.class);
        boolean vibrateOnOff = sharedPreferences.getBoolean("key_vibrate",false);
        boolean alertNotificaitonOnOff = sharedPreferences.getBoolean("key_alert_notification",true);
        int value = 0;


        if(vibrateOnOff) {
            value = Notification.DEFAULT_VIBRATE;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context,R.color.magnitude4))
                .setSmallIcon(R.drawable.ic_add_alert_white_24dp)
                .setContentTitle(context.getString(R.string.notificaiton_title))
                //TODO:Set the text title with kilometer, city and counter of earthuaqe here
                .setContentText("Magnitude of " + newEarthQuakes.getMagnitude() + "earthquake occur in "
                        + earthQuakes.getCityname())

                //.setContentText("Wa g wa la bhai")
                .setDefaults(value)
                //set the pending intent to start the activity when type the notification
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT< Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }


        //If in the setting the Alert Notificaiton is ON
        if(alertNotificaitonOnOff) {
            notificationManager.notify(NEW_EARTHQUAKE_NOTIFICATION_ID, notificationBuilder.build());
        }

        Log.i(TAG, "remindUser: ");
    }


    public static PendingIntent contentIntent(Context context){

        //intent that will open and show the Map activity
        Intent startActivityIntent = new Intent(context, Map.class);

        Bundle bundle = new Bundle();
        bundle.putString(TimelineFragment.MARK_TYPE, TimelineFragment.SINGLE);
        bundle.putDouble(TimelineFragment.LONGITUDE, earthQuakes.getLongitude());
        bundle.putDouble(TimelineFragment.LATITUDE, earthQuakes.getLatitude());
        bundle.putString(TimelineFragment.CITY, earthQuakes.getCityname());
        bundle.putString(TimelineFragment.MAGNITUDE, earthQuakes.getMagnitude());
        bundle.putString(TimelineFragment.DATE, earthQuakes.getTimeStamp());

        startActivityIntent.putExtras(bundle);

        Log.i(TAG, "contentIntent: ");
        //when the intent again created it will keep the intent and update it
        return  PendingIntent.getActivity(context, NEW_EARTHQUAKE_NOTIFICATION_ID,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
