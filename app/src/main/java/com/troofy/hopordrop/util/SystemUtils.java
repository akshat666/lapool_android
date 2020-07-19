package com.troofy.hopordrop.util;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.receiver.AlarmReceiver;

/**
 * Created by akshat
 */
public class SystemUtils {

    public static LatLngBounds buildLatLngBound(LatLng center, double radius){
        LatLngBounds latLngBounds = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(center, radius, 0)).
                include(SphericalUtil.computeOffset(center, radius, 90)).
                include(SphericalUtil.computeOffset(center, radius, 180)).
                include(SphericalUtil.computeOffset(center, radius, 270)).build();
        return latLngBounds;
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isUserDetailedSaved(Context context){
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long authID = sPref.getLong("authID", 0);
        String userID = sPref.getString("userID", null);
        String token = sPref.getString("token", null);

        return authID != 0 && userID != null && token != null;
    }

    public static void clearSharedPref(Context context) {
        context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static boolean isEmpty(TextView etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

    }

    public static boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isTripRunning(Context context){
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long tripID = sPref.getLong(context.getString(R.string.tripIdStr), 0);
        return tripID > 0;
    }

    public static boolean isPickupReqRunning(Context context){
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long pickID = sPref.getLong(context.getString(R.string.pickIdStr), 0);
//        long pickReqTime = sPref.getLong(context.getString(R.string.pickReqTime), 0);

        if (pickID <= 0) {
            return false;
        }
        return true;
    }

    public static void checkLocationService(final Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(null,null);
            dialog.show();
        }
    }

    public static String getCountryZipCode(Context context){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public static String returnAgeGroup(int age){
        if(age <= 12){
            return "0 - 12";
        }else if(age >= 13 && age <= 17){
            return "13 - 17";
        }else if(age >= 18 && age <= 25){
            return "18 - 25";
        }else if(age >= 26 && age <= 30){
            return "26 - 30";
        }else if(age >= 31 && age <= 35){
            return "31 - 35";
        }else if(age >= 36 && age <= 40){
            return "36 - 40";
        }else if(age >= 41 && age <= 50){
            return "41 - 50";
        }else if(age >= 51 && age <= 60){
            return "51 - 60";
        }else if(age >= 61 && age <= 70){
            return "61 - 70";
        }else if(age >= 71){
            return "70+";
        }else{
            return "0";
        }
    }

    /**
     * Stops the trip location update alarm service
     */
    public static void stopTripLocationService(Context context) {
        //stop the trip alarm manager
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AppConstants.TRIP_LOC_ALARM_ID, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        sPref.edit().putBoolean(context.getString(R.string.tripLocationPoll), false).apply();
    }

    /**
     * Stops the alert location update alarm service
     */
    public static void stopAlertLocationService(Context context) {
        //stop the alert alarm manager
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        sPref.edit().remove(context.getString(R.string.alertCount)).apply();
        sPref.edit().remove(context.getString(R.string.alertID)).apply();

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AppConstants.ALERT_ALARM_ID, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        sPref.edit().putBoolean(context.getString(R.string.alertLocationPoll), false).apply();
    }


    public static void clearPickupRequestParameters(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        sPref.edit().remove(context.getString(R.string.pickIdStr)).apply();
//        sPref.edit().remove(context.getString(R.string.pickReqTime)).apply();
//        sPref.edit().remove(context.getString(R.string.pickAddressStr)).apply();
//        sPref.edit().remove(context.getString(R.string.dropAddressStr)).apply();
//        sPref.edit().remove(context.getString(R.string.pickLatStr)).apply();
//        sPref.edit().remove(context.getString(R.string.pickLngStr)).apply();
//        sPref.edit().remove(context.getString(R.string.dropLatStr)).apply();
//        sPref.edit().remove(context.getString(R.string.dropLngStr)).apply();
    }

    public static void sendSMS(String number, String message, Context context){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
        } catch (Exception e) {
            Toast.makeText(context,
                    R.string.smsFail,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void clearTripParameters(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        sPref.edit().remove(context.getString(R.string.tripIdStr)).apply();
        sPref.edit().remove(context.getString(R.string.tripKeyStr)).apply();
        sPref.edit().remove(context.getString(R.string.chatNameStr)).apply();
        sPref.edit().remove(context.getString(R.string.chatAuthIDStr)).apply();
    }

    public static void startLocationAlarm(Context context) {
        //Register an alarm manager
        //If no alarm is set
        SharedPreferences defaultSharedPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,AppConstants.LOCATION_ALARM_ID, alarmIntent, 0);

        //if(!defaultSharedPref.getBoolean("isLocationAlarmSet",false)){
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AppConstants.alarmInterval,
                    pendingIntent);
            defaultSharedPref.edit().putBoolean("isLocationAlarmSet",true).apply();
        //}
    }

    public static boolean isOnlyDigits(String str) {
        String regex = "\\d+";

        return str.matches(regex);

    }


}
