package com.troofy.hopordrop.bean;

import java.io.Serializable;

/**
 * Created by akshat666
 */
public class UserBean implements Serializable {

    private long authID;
    private String token;
    private String name;
    private char gender;
    private String userID;
    private int providerType;
    private FBLoginBean fbLoginBean;

    public FBLoginBean getFbLoginBean() {
        return fbLoginBean;
    }

    public void setFbLoginBean(FBLoginBean fbLoginBean) {
        this.fbLoginBean = fbLoginBean;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getProviderType() {
        return providerType;
    }

    public void setProviderType(int providerType) {
        this.providerType = providerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
