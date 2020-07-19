package com.troofy.hopordrop.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.LocationBean;
import com.troofy.hopordrop.receiver.AlarmReceiver;
import com.troofy.hopordrop.request.AlertUpdateRequest;
import com.troofy.hopordrop.request.LocationUpdateRequest;
import com.troofy.hopordrop.request.TripLocUpdateRequest;
import com.troofy.hopordrop.util.SystemUtils;

/**
 * Created by akshat666
 */
public class AlarmService extends AbstractSpiceService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "AlarmService";

    private long authID;
    private SharedPreferences sPref;
    private Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    private Intent intent;
    private boolean isTripLocation;
    private boolean isAlertLocation;
    private long tripID;
    private long alertID;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        buildGoogleApiClient();
        this.intent = intent;

        //extract the trip location flag - this mean sit is trip locations update (when trip is running)
        this.isTripLocation = this.intent.getBooleanExtra(this.getString(R.string.tripLocationPoll),false);


        //is the service for alert location polling
        this.isAlertLocation = this.intent.getBooleanExtra(this.getString(R.string.alertLocationPoll),false);

        return START_NOT_STICKY;
    }

    private void sendLocation() {

        sPref = getSharedPreferences(getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        this.authID = sPref.getLong("authID",0);
        String token = sPref.getString("token",null);
        this.tripID = sPref.getLong(this.getString(R.string.tripIdStr),0);

        LocationBean locationBean = new LocationBean();
        locationBean.setLat(mLastLocation.getLatitude());
        locationBean.setLng(mLastLocation.getLongitude());

        Genson gen = new Genson();
        String json = gen.serialize(locationBean);

        //If location update is for the on going trip
        if(isTripLocation){

            if(tripID <= 0){
                return;
            }
            TripLocUpdateRequest updateRequest = new TripLocUpdateRequest(authID, tripID, json, token, this);
            this.spiceManager.execute(updateRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new TripLocUpdateListener());
        }
        else if(isAlertLocation){
            alertID = sPref.getLong(this.getString(R.string.alertID),0);
            if(alertID <= 0){
                return;
            }
            int alertCount = sPref.getInt(this.getString(R.string.alertCount),0);
            if(alertCount > 24){
                SystemUtils.stopAlertLocationService(this);
                return;
            }
                alertCount++;
                sPref.edit().putInt(getString(R.string.alertCount),alertCount).apply();

            AlertUpdateRequest updateRequest = new AlertUpdateRequest(alertID, authID, json, token, this);
            this.spiceManager.execute(updateRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new AlertLocUpdateListener());
        }
        //if location update is a regular polling update
        else{
            LocationUpdateRequest updateRequest = new LocationUpdateRequest(authID,json,token,this);
            this.spiceManager.execute(updateRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new LocationUpdateListener());
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            ApplicationVariables appVariables = (ApplicationVariables) this.getApplicationContext();

            if (mLastLocation != null) {
                appVariables.setCurrentLocation(mLastLocation);
            } else {
                mLastLocation = appVariables.getCurrentLocation();
            }

            if (mLastLocation == null) {
                return;
            }

            sendLocation();
            stopSelf();
        }catch (Exception ex){
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private class LocationUpdateListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }

        @Override
        public void onRequestSuccess(HttpResponse response) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }
    }

    private class TripLocUpdateListener implements com.octo.android.robospice.request.listener.RequestListener<HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }

        @Override
        public void onRequestSuccess(HttpResponse httpResponse) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }
    }

    private class AlertLocUpdateListener implements com.octo.android.robospice.request.listener.RequestListener<HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }

        @Override
        public void onRequestSuccess(HttpResponse httpResponse) {
            AlarmReceiver.completeWakefulIntent(intent);
            stopSelf();
        }
    }
}
