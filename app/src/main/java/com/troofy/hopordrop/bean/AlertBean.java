package com.troofy.hopordrop.bean;

import com.google.api.client.util.Key;

import java.io.Serializable;

/**
 * Created by akshat666
 */
public class AlertBean implements Serializable {

    @Key
    private long alertID;
    private long tripID;
    @Key
    private long authID;
    private double lat;
    private double lng;
    @Key
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getAlertID() {
        return alertID;
    }

    public void setAlertID(long alertID) {
        this.alertID = alertID;
    }

    public long getTripID() {
        return tripID;
    }

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    public long getAuthID() {
        return authID;
    }

    public void setAuthID(long authID) {
        this.authID = authID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
