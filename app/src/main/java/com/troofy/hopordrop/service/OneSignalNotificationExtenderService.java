package com.troofy.hopordrop.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.PoolMainActivity;
import com.troofy.hopordrop.activity.TripActivity;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akshat666
 * This class runs outside of the UI thread
 */
public class OneSignalNotificationExtenderService extends NotificationExtenderService {
    private SharedPreferences defaultSharedPref;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {

        defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        boolean isActive = notification.isAppInFocus;

        //notification.payload.additionalData.getString("data_type")

        JSONObject data = notification.payload.additionalData;
        Intent i;
        try {
            int dataType = 0;

            dataType = data.getInt("data_type");
            int count = defaultSharedPref.getInt(this.getString(R.string.noty_count), 0);

            switch (dataType) {
                case AppConstants.PUSH_TYPE_PICKREQ_CREATED:
                    if (count >= 3) {
                        OneSignal.enableSound(false);
                        OneSignal.enableVibrate(false);
                    } else {
                        count++;
                        defaultSharedPref.edit().putInt(this.getString(R.string.noty_count), count).apply();
                    }
                    break;

                case AppConstants.PUSH_TYPE_TRIP_ENDED:

                    SystemUtils.stopTripLocationService(this);
                    SystemUtils.clearTripParameters(this);
                    if (isActive) {
                        i = new Intent(this, PoolMainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(i);
//                        Toast.makeText(this,
//                                getString(R.string.tripEnded), Toast.LENGTH_SHORT)
//                                .show();
                    }
                    break;

                case AppConstants.PUSH_TYPE_PICKREQ_ACCEPTED:
                    long tripID = 0;
                    try {
                        tripID = data.getLong("tripID");
                        SystemUtils.clearPickupRequestParameters(this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tripID > 0) {
                        defaultSharedPref.edit().putLong(this.getString(R.string.tripIdStr), tripID).apply();
                    }
                    if(isActive){
                        i = new Intent();
                        i.setClass(this, TripActivity.class);
                        i.setAction(TripActivity.class.getName());
                        i.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(i);
                    }

            }

        } catch (Throwable t) {

        }

        return false;
    }
}
