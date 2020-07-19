package com.troofy.hopordrop.bean;

import java.io.Serializable;

/**
 * Created by akshat666 on 10/02/16.
 */
public class TripBean implements Serializable{

    private String hopperName;
    private String dropperName;

    private long tripID;

    private long tripStartTime;
    private long tripEndTime;

    private boolean isNoSmoking;
    private boolean isGirlsOnly;
    private boolean music;

    //userID of the person who requested a pickup
    private long hopperID;
    //UserID of the person who accepted to Drop a user
    private long dropperID;

    //Pickup location when it was requested
    private double pickupLat;
    private double pickupLng;

    //Drop off location when it was requested
    private double dropLat;
    private double dropLng;

    //Dropper and hopper location when req is confirmed
    private double dropperStartLat;
    private double dropperStartLng;

    private double pickerStartLat;
    private double pickerStartLng;

    //Dropper and hopper location when they end the trip
    private double dropperStopLat;
    private double dropperStopLng;

    private double hopperStopLat;
    private double hopperStopLng;

    private String pickAddress;
    private String dropAddress;

    public String getHopperName() {
        return hopperName;
    }

    public void setHopperName(String hopperName) {
        this.hopperName = hopperName;
    }

    public String getDropperName() {
        return dropperName;
    }

    public void setDropperName(String dropperName) {
        this.dropperName = dropperName;
    }

    public long getTripID() {
        return tripID;
    }

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    public long getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(long tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public long getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(long tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public boolean isNoSmoking() {
        return isNoSmoking;
    }

    public void setNoSmoking(boolean noSmoking) {
        isNoSmoking = noSmoking;
    }

    public boolean isGirlsOnly() {
        return isGirlsOnly;
    }

    public void setGirlsOnly(boolean girlsOnly) {
        isGirlsOnly = girlsOnly;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public long getHopperID() {
        return hopperID;
    }

    public void setHopperID(long hopperID) {
        this.hopperID = hopperID;
    }

    public long getDropperID() {
        return dropperID;
    }

    public void setDropperID(long dropperID) {
        this.dropperID = dropperID;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDropLat() {
        return dropLat;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public double getDropLng() {
        return dropLng;
    }

    public void setDropLng(double dropLng) {
        this.dropLng = dropLng;
    }

    public double getDropperStartLat() {
        return dropperStartLat;
    }

    public void setDropperStartLat(double dropperStartLat) {
        this.dropperStartLat = dropperStartLat;
    }

    public double getDropperStartLng() {
        return dropperStartLng;
    }

    public void setDropperStartLng(double dropperStartLng) {
        this.dropperStartLng = dropperStartLng;
    }

    public double getPickerStartLat() {
        return pickerStartLat;
    }

    public void setPickerStartLat(double pickerStartLat) {
        this.pickerStartLat = pickerStartLat;
    }

    public double getPickerStartLng() {
        return pickerStartLng;
    }

    public void setPickerStartLng(double pickerStartLng) {
        this.pickerStartLng = pickerStartLng;
    }

    public double getDropperStopLat() {
        return dropperStopLat;
    }

    public void setDropperStopLat(double dropperStopLat) {
        this.dropperStopLat = dropperStopLat;
    }

    public double getDropperStopLng() {
        return dropperStopLng;
    }

    public void setDropperStopLng(double dropperStopLng) {
        this.dropperStopLng = dropperStopLng;
    }

    public double getHopperStopLat() {
        return hopperStopLat;
    }

    public void setHopperStopLat(double hopperStopLat) {
        this.hopperStopLat = hopperStopLat;
    }

    public double getHopperStopLng() {
        return hopperStopLng;
    }

    public void setHopperStopLng(double hopperStopLng) {
        this.hopperStopLng = hopperStopLng;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }
}
