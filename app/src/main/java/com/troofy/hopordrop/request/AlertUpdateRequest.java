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
public class AlertUpdateRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {


    private long alertID;
    private long authID;
    private String json;
    private String token;
    private Context context;

    public AlertUpdateRequest(long alertID, long authID, String json,  String token, Context context){
        super(HttpResponse.class);
        this.alertID = alertID;
        this.authID = authID;
        this.json = json;
        this.token = token;
        this.context = context;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.ALERT_UPDATE_LOCATION) + "/"+alertID;
        GenericUrl genericUrl = new GenericUrl(url);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(genericUrl, ByteArrayContent.fromString("application/json", json));
        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));
        request.setConnectTimeout(30000);

        return request.execute();
    }
}
