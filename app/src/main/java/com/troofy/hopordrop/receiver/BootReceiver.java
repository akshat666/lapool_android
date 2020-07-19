package com.troofy.hopordrop.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.util.AppConstants;

/**
 * Created by akshat666
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AppConstants.alarmInterval,
                    pendingIntent);
            SharedPreferences defaultSharedPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = defaultSharedPref.edit();
            editor.putBoolean("isLocationAlarmSet",true);
            editor.commit();
        }
    }
}
