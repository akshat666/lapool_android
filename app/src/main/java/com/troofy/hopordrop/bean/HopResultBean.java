/*
 * ************************************************************************
 *  *
 *  * TROOFY LABS - CONFIDENTIAL
 *  * __________________
 *  *  Copyright (c) 2017.
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

/**
 * Created by akshat666
 */

public class HopResultBean implements Serializable {

    @Key
    private long pickID;
    @Key
    private long notifiedUsers;

    public long getPickID() {
        return pickID;
    }

    public void setPickID(long pickID) {
        this.pickID = pickID;
    }

    public long getNotifiedUsers() {
        return notifiedUsers;
    }

    public void setNotifiedUsers(long notifiedUsers) {
        this.notifiedUsers = notifiedUsers;
    }
}
