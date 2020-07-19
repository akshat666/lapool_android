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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by akshat666
 */
public class ConfirmTripRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private long pickID;
    private long userAuthID;
    private Context context;
    private String token;
    private double dropperLat;
    private double dropperLng;

    public ConfirmTripRequest(long pickID, long userAuthID, Context context, String token, double lat, double lng) {
        super(HttpResponse.class);
        this.pickID = pickID;
        this.userAuthID = userAuthID;
        this.context = context;
        this.token = token;
        this.dropperLat = lat;
        this.dropperLng = lng;
    }


    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.CONFIRM_TRIP_URL);

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("pickID", pickID);
        json.put("userAuthID", userAuthID);
        json.put("dropperLat", this.dropperLat);
        json.put("dropperLng", this.dropperLng);
        final HttpContent content = new JsonHttpContent(new JacksonFactory(), json);

        final HttpRequest request = getHttpRequestFactory().buildPostRequest(new GenericUrl(url), content);
        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",userAuthID));

        request.setConnectTimeout(30000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        return request.execute();

    }
}
