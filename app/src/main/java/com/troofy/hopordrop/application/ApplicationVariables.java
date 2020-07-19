package com.troofy.hopordrop.application;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.google.android.gms.common.api.GoogleApiClient;
import com.onesignal.OneSignal;
import com.troofy.hopordrop.handler.OneSignalNotificationOpenedHandler;

/**
 *
 */
public class ApplicationVariables extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler(ApplicationVariables.this.getApplicationContext()))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .init();
        AccountKit.initialize(getApplicationContext());

    }

    private Location currentLocation;
    private GoogleApiClient googleApiClient;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
