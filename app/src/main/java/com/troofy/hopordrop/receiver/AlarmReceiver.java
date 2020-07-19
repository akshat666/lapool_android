package com.troofy.hopordrop.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.service.AlarmService;

/**
 * Created by akshat666
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isTripAlarm = intent.getBooleanExtra(context.getString(R.string.tripLocationPoll),false);
        boolean isAlertAlarm = intent.getBooleanExtra(context.getString(R.string.alertLocationPoll),false);

        Intent i = new Intent(context, AlarmService.class);
        i.putExtra(context.getString(R.string.tripLocationPoll), isTripAlarm);
        i.putExtra(context.getString(R.string.alertLocationPoll), isAlertAlarm);
        startWakefulService(context,i);
    }
}
