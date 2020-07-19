package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

/**
 * Created by akshat666
 */
public class TripEndRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private String json;
    private Context context;
    private String token;
    private long authID;

    public TripEndRequest(String jsonString, Context context, String token, long authID){
        super(HttpResponse.class);
        this.json = jsonString;
        this.context = context;
        this.token = token;
        this.authID = authID;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.CANCEL_TRIP);
        GenericUrl genericUrl = new GenericUrl(url);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", json));

        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));
        request.setConnectTimeout(60000);
        return request.execute();
    }
}
