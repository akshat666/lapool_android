package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

/**
 * Created by akshat666
 */
public class TokenUpdateRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private String json;
    private Context context;
    private long authID;

    public TokenUpdateRequest(Context context, String json, long authID ) {
        super(HttpResponse.class);
        this.context = context;
        this.json = json;
        this.authID = authID;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.FB_TOKEN_UPDATE_URL)+"/"+authID;

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", json));
        request.setConnectTimeout(60000);
        return request.execute();

    }
}
