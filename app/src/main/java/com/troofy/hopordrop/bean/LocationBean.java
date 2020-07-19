package com.troofy.hopordrop.bean;

import java.io.Serializable;

/**
 * Created by akshat666
 */
public class LocationBean implements Serializable {

    private double lat;
    private double lng;

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
