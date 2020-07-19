package com.troofy.hopordrop.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.ChatActivity;
import com.troofy.hopordrop.activity.GiveDropActivity;
import com.troofy.hopordrop.activity.PoolMainActivity;
import com.troofy.hopordrop.activity.TripActivity;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akshat666
 */
public class OneSignalNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    private Context context;
    private SharedPreferences sPref;
    private AlertDialog.Builder builder;

    public OneSignalNotificationOpenedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult openedResult) {

        OSNotification notification = openedResult.notification;
        JSONObject additionalData = notification.payload.additionalData;
        boolean isStacked = notification.groupedNotifications != null? true : false;
        OSNotificationAction.ActionType actionType = openedResult.action.type;
        boolean isActive = notification.isAppInFocus;

        sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        Intent i;
        int dataType = 0;

//        boolean isStacked = additionalData.has("stacked_notifications");

        if (!isStacked) {
            try {
                dataType = additionalData.getInt("data_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean isTripOn = (sPref.getLong(context.getString(R.string.tripIdStr), 0) > 0);
            boolean isHopReqOn = (sPref.getLong(context.getString(R.string.pickIdStr), 0) > 0);

            switch (dataType) {

                //When some friend creates a pickupRequest
                case AppConstants.PUSH_TYPE_PICKREQ_CREATED:
                    // if user already in a trip or pick request - do not redirect user
                    if (!isActive && !isTripOn && !isHopReqOn) {
                        i = new Intent(context, GiveDropActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("selectedTab", 1);
                        context.startActivity(i);
                    }
                    break;
                //The user's pick request was accepted
                // User is a hopper here
                case AppConstants.PUSH_TYPE_PICKREQ_ACCEPTED:

                    long tripID = 0;
                    try {
                        tripID = additionalData.getLong("tripID");
                        SystemUtils.clearPickupRequestParameters(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tripID == 0) {
                        return;
                    }
                    sPref.edit().putLong(context.getString(R.string.tripIdStr), tripID).apply();
                    i = new Intent();
                    i.setClass(context, TripActivity.class);
                    i.setAction(TripActivity.class.getName());
                    i.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    break;

                case AppConstants.PUSH_TYPE_TRIP_ENDED:
                    sPref.edit().remove(context.getString(R.string.tripIdStr)).apply();
                    sPref.edit().remove(context.getString(R.string.chatNameStr)).apply();
                    sPref.edit().remove(context.getString(R.string.chatAuthIDStr)).apply();

                    i = new Intent(context, PoolMainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
//                    Toast.makeText(context,
//                            context.getString(R.string.tripEnded), Toast.LENGTH_SHORT)
//                            .show();
                    break;

                case AppConstants.PUSH_TYPE_CHAT:
                    if(isTripOn)
                    {
//                        Toast.makeText(context,
//                                "New message : " + message, Toast.LENGTH_SHORT)
//                                .show();
                    }
                    if (!isActive && isTripOn) {
                        i = new Intent(context, ChatActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                        break;
                    }

            }
        }
        //if stacked notifications
        else {
            try {
                notification.groupedNotifications.get(0);
//                JSONArray array = (JSONArray) additionalData.get("stacked_notifications");
//                JSONObject obj = (JSONObject) array.get(0);
                    JSONObject obj = notification.groupedNotifications.get(0).additionalData;
                //If pick reqs notifications
                if (obj.getInt("data_type") == AppConstants.PUSH_TYPE_PICKREQ_CREATED) {
                    OneSignal.enableSound(true);
                    OneSignal.enableVibrate(true);
                    sPref.edit().putInt(context.getString(R.string.noty_count),0).apply();
                    //Check if trip is running
                    if(SystemUtils.isTripRunning(context) || SystemUtils.isPickupReqRunning(context)){
                        i = new Intent(context, PoolMainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                    i = new Intent(context, GiveDropActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("selectedTab", 1);
                    context.startActivity(i);
                }
                //If chat notifications
                if(obj.getInt("data_type") == AppConstants.PUSH_TYPE_CHAT){
                    i = new Intent(context, ChatActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
