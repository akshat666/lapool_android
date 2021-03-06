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
 * Class that update the user location during a running trip
 *
 */
public class TripLocUpdateRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private long authID;
    private long tripID;
    private String json;
    private String token;
    private Context context;

    public TripLocUpdateRequest(long authID, long tripID, String json,  String token, Context context){
        super(HttpResponse.class);
        this.authID = authID;
        this.tripID = tripID;
        this.json = json;
        this.token = token;
        this.context = context;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.TRIP_UPDATE_LOCATION) + "/"+tripID+ "/"+authID;
        GenericUrl genericUrl = new GenericUrl(url);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(genericUrl, ByteArrayContent.fromString("application/json", json));
        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));
        request.setConnectTimeout(30000);

        return request.execute();
    }
}
