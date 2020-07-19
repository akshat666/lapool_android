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

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PathBean;

/**
 * Created by akshat666
 */

public class SavePathRequest extends GoogleHttpClientSpiceRequest<HttpResponse>{

    private String pathJson;
    private String token;
    private long authID;
    private Context context;

    public SavePathRequest(String token, long authID, String pathJson, Context context){
        super(HttpResponse.class);
        this.pathJson = pathJson;
        this.token = token;
        this.authID =authID;
        this.context = context;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.SAVE_PATH);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", pathJson));
        request.setHeaders((new HttpHeaders()).set("token",token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);
        return request.execute();
    }
}
