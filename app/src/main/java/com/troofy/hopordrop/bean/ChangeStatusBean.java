package com.troofy.hopordrop.bean;

import java.io.Serializable;

/**
 * Created by akshat666
 */
public class ChangeStatusBean implements Serializable {

    private int status;
    private long id;
    private long authID;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthID() {
        return authID;
    }

    public void setAuthID(long authID) {
        this.authID = authID;
    }
}
