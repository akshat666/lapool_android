package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.AlertBean;

/**
 * Created by akshat666 on 12/06/16.
 */
public class AlertRequest extends GoogleHttpClientSpiceRequest<AlertBean> {

    private Context context;
    private  String token;
    private String json;
    private long authID;

    public AlertRequest(Context context, long authID, String token, String json){
        super(AlertBean.class);
        this.context = context;
        this.authID = authID;
        this.token = token;
        this.json = json;

    }

    @Override
    public AlertBean loadDataFromNetwork() throws Exception {
        //Send request to server
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.SAVE_ALERT);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", json));
        request.setHeaders((new HttpHeaders()).set("token",token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);
        HttpResponse response = request.execute();
        return response.parseAs(AlertBean.class);
    }
}
