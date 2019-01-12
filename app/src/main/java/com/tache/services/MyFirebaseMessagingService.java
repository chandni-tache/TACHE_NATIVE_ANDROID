package com.tache.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tache.R;
import com.tache.activity.MainActivity;
import com.tache.events.NotificationEvent;
import com.tache.utils.Helper;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;


/**
 * Created by mayank on 2/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "CHECK";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Payload: " + String.valueOf(remoteMessage.getData()));
            if (Helper.isLoggedIn(this) && remoteMessage.getData().containsKey("message")) {
                sendNotification(remoteMessage.getData().get("message"));
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification data payload: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1 /* Request code */, new Intent("com.tache.NOTIFICATION"), PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ? R.drawable.noti_white : R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(new Random().nextInt() /* ID of notification */, notificationBuilder.build());
    }
}
