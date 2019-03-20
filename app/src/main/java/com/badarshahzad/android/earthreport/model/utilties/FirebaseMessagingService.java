package com.badarshahzad.android.earthreport.model.utilties;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.badarshahzad.android.earthreport.R;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by root on 8/19/18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServic";
    private static int NEW_EARTHQUAKE_NOTIFICATION_ID = 1110;
    private static String NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID = "earthquake_notification_channel";
    private static int count = 0;

//    EarthQuakes earthQuakes = new EarthQuakes();

    public static PendingIntent contentIntent(Context context) {

        //intent that will open and show the Map activity
        Intent startActivityIntent = new Intent(context, com.badarshahzad.android.earthreport.controller.main.EarthquakeActivity.class);

//        Bundle bundle = new Bundle();
//        bundle.putString(TimelineFragment.MARK_TYPE, TimelineFragment.SINGLE);
//        bundle.putDouble(TimelineFragment.LONGITUDE, earthQuakes.getLongitude());
//        bundle.putDouble(TimelineFragment.LATITUDE, earthQuakes.getLatitude());
//        bundle.putString(TimelineFragment.CITY, earthQuakes.getCityname());
//        bundle.putString(TimelineFragment.MAGNITUDE, earthQuakes.getMagnitude());
//        bundle.putString(TimelineFragment.DATE, earthQuakes.getTimeStamp());
//
//        startActivityIntent.putExtras(bundle);
//
//        Log.i(TAG, "contentIntent: ");
        //when the intent again created it will keep the intent and update it
        return PendingIntent.getActivity(context, NEW_EARTHQUAKE_NOTIFICATION_ID, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "Notification Message TITLE: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "Notification Message BODY: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Message DATA: " + remoteMessage.getData().toString());

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(), remoteMessage.getData());

    }

    //This method is only generating push notification
    private void sendNotification(String messageTitle, String messageBody, Map<String, String> row) {
//        PendingIntent contentIntent = null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, NEW_EARTHQUAKE_NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_alert_white_24dp))
                .setSmallIcon(R.drawable.ic_add_alert_white_24dp)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent(this));


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(count, notificationBuilder.build());
        count++;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

    }
}