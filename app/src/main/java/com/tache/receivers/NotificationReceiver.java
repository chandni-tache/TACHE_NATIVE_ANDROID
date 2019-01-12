package com.tache.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tache.activity.MainActivity;
import com.tache.events.NotificationEvent;
import com.tache.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by a_man on 4/16/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPrefsUtils sharedPrefsUtils = SharedPrefsUtils.getInstance(context);

        if (sharedPrefsUtils.getBooleanPreference(SharedPrefsUtils.IN_APP, false)) {
            EventBus.getDefault().post(new NotificationEvent());
        } else {
            Intent open = new Intent(context, MainActivity.class);
            open.putExtra("notification", true);
            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(open);
        }

    }
}
