package com.troofy.hopordrop.bean;

import com.google.api.client.util.Key;

import java.io.Serializable;

/**
 * Created by akshat666
 * Check what all things are running for the user currently i.e. PickupReq or a Trip is running
 */
public class UserCurrentStatusBean implements Serializable {

    @Key
    private long pickReqID;
    @Key
    private int pickReqStatus;
    @Key
    private long tripID;
    @Key
    private int tripStatus;
    @Key
    private int userStatus;

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public long getPickReqID() {
        return pickReqID;
    }

    public void setPickReqID(long pickReqID) {
        this.pickReqID = pickReqID;
    }

    public int getPickReqStatus() {
        return pickReqStatus;
    }

    public void setPickReqStatus(int pickReqStatus) {
        this.pickReqStatus = pickReqStatus;
    }

    public long getTripID() {
        return tripID;
    }

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    public int getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(int tripStatus) {
        this.tripStatus = tripStatus;
    }
}
