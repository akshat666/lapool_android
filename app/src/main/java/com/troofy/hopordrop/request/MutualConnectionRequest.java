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

package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.MutualFriendsBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshat666
 */

public class MutualConnectionRequest extends GoogleHttpClientSpiceRequest<MutualFriendsBean> {

    private long authID;
    private String token;
    private long userInReqAuthID;
    private Context context;

    public MutualConnectionRequest(long authID, String token, long userInReqAuthID, Context context){
        super(MutualFriendsBean.class);
        this.authID = authID;
        this.token = token;
        this.userInReqAuthID = userInReqAuthID;
        this.context = context;
    }

    @Override
    public MutualFriendsBean loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.MUTUAL_CONN);

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("authID", authID);
        json.put("userInReqAuthID", userInReqAuthID);

        final HttpContent content = new JsonHttpContent(new JacksonFactory(), json);

        final HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(url), content);
        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));

        request.setConnectTimeout(30000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        return request.execute().parseAs(MutualFriendsBean.class);
    }
}
