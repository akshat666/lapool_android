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
public class PickupReqCancelRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private String jsonString;
    private Context context;
    private String token;
    private long authID;

    public PickupReqCancelRequest(String json, String token, long authID, Context context){
        super(HttpResponse.class);
        this.jsonString = json;
        this.context = context;
        this.token = token;
        this.authID = authID;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.CANCEL_PICKUPREQUEST);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", jsonString));

        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));
        request.setConnectTimeout(60000);

        return request.execute();
    }
}
