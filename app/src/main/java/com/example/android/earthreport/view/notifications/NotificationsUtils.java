package com.example.android.earthreport.view.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.earthreport.R;
import com.example.android.earthreport.view.home.EarthquakeActivity;

/**
 * Created by root on 12/17/17.
 */

public class NotificationsUtils {

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

    private static int NEW_EARTHQUAKE_PENDING_INTENT_ID =1111;

    private static String NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID = "earthquake_notification_channel";


    public static void remindUser(Context context){

        //get the notification manager from context.getSystemService
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //create a notification channel for Android devices
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notificationChannel = new NotificationChannel(
                    NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID,context.getString(R.string.main_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH);

            //notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        // To check the Vibrate and Notification turn ON/OFF

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean vibrateOnOff = sharedPreferences.getBoolean("key_vibrate",false);
        boolean alertNotificaitonOnOff = sharedPreferences.getBoolean("key_alert_notification",true);
        int value = 0;
        if(vibrateOnOff) {
            value = Notification.DEFAULT_VIBRATE;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context,R.color.magnitude4))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(context.getString(R.string.notificaiton_title))
                .setContentText(context.getString(R.string.notification_summary))

                .setDefaults(value)
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

    }

    public static PendingIntent contentIntent(Context context){

        //intent that will open the   Earthquake Main activity
        Intent startActivityIntent = new Intent(context, EarthquakeActivity.class);

        //when the intent again created it will keep the intent and update it
        return  PendingIntent.getActivity(context, NEW_EARTHQUAKE_NOTIFICATION_ID,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //this method return a bitmap necessery to decode a bitmap needed for the notification
    private  static Bitmap largIcon(Context context){

        Resources res = context.getResources();
        Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.ic_notifications_black_24dp);

        return icon;
    }
}
