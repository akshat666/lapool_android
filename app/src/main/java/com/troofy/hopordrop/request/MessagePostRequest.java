package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

/**
 * Created by akshat666
 */
public class MessagePostRequest extends GoogleHttpClientSpiceRequest<Long> {

    private String json;
    private String token;
    private Context context;
    private long authID;

    public MessagePostRequest(String json, String token, long authID, Context context) {
        super(Long.class);
        this.json = json;
        this.token = token;
        this.context = context;
        this.authID = authID;
    }

    @Override
    public Long loadDataFromNetwork() throws Exception {
        //Send request to server
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.POST_MSG);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", json));
        request.setHeaders((new HttpHeaders()).set("token",token).set("authID",authID));
        request.setConnectTimeout(60000);
        return request.execute().parseAs(Long.class);
    }
}
