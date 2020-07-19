package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PickUpRequestBean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshat
 */
public class RidesListRequest extends GoogleHttpClientSpiceRequest<PickUpRequestBean.PickReqList> {

    private Context context;
    private long authID;
    private String token;
    private int pageNumber;
    private int pageSize;
    private String lat;
    private String lng;
    private boolean publicListCall = false;

    public RidesListRequest(Context context, long authId, String token, String lat, String lng, int pageNumber, int pageSize, boolean isPublic) {
        super(PickUpRequestBean.PickReqList.class);
        this.authID = authId;
        this.context = context;
        this.token = token;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.lat = lat;
        this.lng = lng;
        this.publicListCall = isPublic;
    }

    @Override
    public PickUpRequestBean.PickReqList loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.PICKUP_REQUEST_LIST_URL)+"/"+authID+"/"+publicListCall;

        GenericUrl genericUrl = new GenericUrl(url);

        genericUrl.set("pageNumber", Integer.toString(this.pageNumber));
        genericUrl.set("pageSize", Integer.toString(this.pageSize));
        genericUrl.set("lat",lat);
        genericUrl.set("lng",lng);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);
        HttpResponse response = request.execute();
        return response.parseAs(PickUpRequestBean.PickReqList.class);

    }
}
