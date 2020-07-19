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

import java.util.ArrayList;
import java.util.List;

/**
 * @author akshat666
 */
public class MutualFriendsBean {

    @Key
    private long totalMutualFriends;
    @Key
    private List<User> user = new ArrayList<User>();

    public long getTotalMutualFriends() {
        return totalMutualFriends;
    }

    public void setTotalMutualFriends(long totalMutualFriends) {
        this.totalMutualFriends = totalMutualFriends;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }


    public static class User {
        public User() {
        }

        @Key
        public String name;
        @Key
        public String picUrl;

    }

}

