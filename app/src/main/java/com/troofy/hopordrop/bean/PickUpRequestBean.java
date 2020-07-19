/*
 * ************************************************************************
 *  *
 *  * TROOFY LABS - CONFIDENTIAL
 *  * __________________
 *  *  Copyright (c) 2016.
 *  *
 *  *  All Rights Reserved.
 *  *
 *  * NOTICE:  All information contained herein is, and remains
 *  * the property of Troofy Labs(OPC) Private Limited and its suppliers,
 *  * if any.  The intellectual and technical concepts contained
 *  * herein are proprietary to Troofy Labs(OPC) Private Limited
 *  * and its suppliers and may be covered by U.S. and Foreign Patents,
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * from Troofy Labs(OPC) Private Limited.
 *
 */

package com.troofy.hopordrop.bean;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by akshat
 */
public class PickUpRequestBean implements Serializable{
    @Key
    private long authID;
    @Key
    private long pickID;
    @Key
    private String name;
    @Key
    private double pickUpLat;
    @Key
    private double pickUpLng;
    @Key
    private double dropLat;
    @Key
    private double dropLng;
    @Key
    private String userID;
    @Key
    private String pickAddress;
    @Key
    private String dropAddress;
    @Key
    private boolean girlsOnly;
    @Key
    private boolean isSmoking;
    @Key
    private boolean openToAll;
    @Key
    private boolean music;
    @Key
    private char gender;
    @Key
    private int age;
    @Key
    private double distanceFromUser;
    @Key
    private long created;
    @Key
    private int seats;
    @Key
    private java.util.List<Integer> networkLinked;

    public static class PickReqList extends ArrayList<PickUpRequestBean> {

        @Key
        private List<PickUpRequestBean> pickUpRequestBeanList;

        public List<PickUpRequestBean> getPickUpRequestBeanList() {
            return pickUpRequestBeanList;
        }

        public void setPickUpRequestBeanList(List<PickUpRequestBean> pickUpRequestBeanList) {
            this.pickUpRequestBeanList = pickUpRequestBeanList;
        }
    }

    public long getPickID() {
        return pickID;
    }

    public void setPickID(long pickID) {
        this.pickID = pickID;
    }



    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public long getAuthID() {
        return authID;
    }

    public void setAuthID(long authID) {
        this.authID = authID;
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

    public double getPickUpLat() {
        return pickUpLat;
    }

    public void setPickUpLat(double pickUpLat) {
        this.pickUpLat = pickUpLat;
    }

    public double getPickUpLng() {
        return pickUpLng;
    }

    public void setPickUpLng(double pickUpLng) {
        this.pickUpLng = pickUpLng;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isGirlsOnly() {
        return girlsOnly;
    }

    public void setGirlsOnly(boolean girlsOnly) {
        this.girlsOnly = girlsOnly;
    }

    public boolean isSmoking() {
        return isSmoking;
    }

    public void setSmoking(boolean smoking) {
        isSmoking = smoking;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.List getNetworkLinked() {
        return networkLinked;
    }

    public void setNetworkLinked(java.util.List<Integer> networkLinked) {
        this.networkLinked = networkLinked;
    }

    public boolean isOpenToAll() {
        return openToAll;
    }

    public void setOpenToAll(boolean openToAll) {
        this.openToAll = openToAll;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
