package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.HopResultBean;

/**
 * Created by akshat
 */
public class PickUpRequest extends GoogleHttpClientSpiceRequest<HopResultBean>{

    private String pickUpReqJSON;
    private Context context;
    private String token;
    private long authID;

    public PickUpRequest(String pickUpReqJSON, String token,long authID, Context context){
        super(HopResultBean.class);
        this.pickUpReqJSON = pickUpReqJSON;
        this.context = context;
        this.token = token;
        this.authID = authID;

    }

    @Override
    public HopResultBean loadDataFromNetwork() throws Exception {

        //Send request to server
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.PICKUP_REQUEST_URL);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", pickUpReqJSON));
        request.setHeaders((new HttpHeaders()).set("token",token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);
        HttpResponse result = request.execute();
        return result.parseAs(HopResultBean.class);
    }
}
