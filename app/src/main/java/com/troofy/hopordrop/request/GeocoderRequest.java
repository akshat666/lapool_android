package com.troofy.hopordrop.request;

import android.content.Context;
import android.location.Geocoder;

import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.util.List;

/**
 * Created by akshat
 */
public class GeocoderRequest extends GoogleHttpClientSpiceRequest<List> {

    private double lat;
    private double lng;
    private int max;
    private Context context;

    public GeocoderRequest(Double lat, Double lng, int max, Context context){
        super(List.class);
        this.lat = lat;
        this.lng = lng;
        this.max = max;
        this.context = context;
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        Geocoder geo = new Geocoder(context);
        List addresses = geo.getFromLocation(lat,lng,max);
        return addresses;
    }
}
