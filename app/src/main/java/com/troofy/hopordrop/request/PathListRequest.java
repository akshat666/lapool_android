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
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PathBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshat666
 */

public class PathListRequest extends GoogleHttpClientSpiceRequest<List> {

    private Context context;
    private String token;
    private long authID;

    public PathListRequest(Context context, String token, long authID) {
        super(List.class);
        this.context = context;
        this.authID = authID;
        this.token = token;
    }

    @Override
    public List loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.FETCH_PATH_LIST);

        GenericUrl genericUrl = new GenericUrl(url);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));
        request.setConnectTimeout(60000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(List.class);
    }
}
