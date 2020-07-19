package com.troofy.hopordrop.bean;

import java.io.Serializable;

/**
 * Created by akshat666
 */
public class UserChannelBean implements Serializable {

    private long authID;
    private String email;
    private String channel;
    private int status;

    public long getAuthID() {
        return authID;
    }

    public void setAuthID(long authID) {
        this.authID = authID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
