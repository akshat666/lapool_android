package com.troofy.hopordrop.bean;

/**
 * Created by Akshat
 */
public class PlaceAutocomplete {

    public CharSequence placeId;
    public CharSequence description;

    public PlaceAutocomplete(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}