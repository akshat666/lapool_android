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
 * Created by akshat
 */
public class SaveEmailRequest extends GoogleHttpClientSpiceRequest<HttpResponse> {

    private String emailReqJson;
    private Context context;
    private String token;
    private long authID;

    public SaveEmailRequest(String emailReqJson, String token, long authID, Context context) {
        super(HttpResponse.class);
        this.context = context;
        this.emailReqJson = emailReqJson;
        this.token = token;
        this.authID =authID;
    }

    @Override
    public HttpResponse loadDataFromNetwork() throws Exception {
        //Send request to server
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.SAVE_VERIFY_EMAIL_URL);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", emailReqJson));
        request.setHeaders((new HttpHeaders()).set("token",token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);
        return request.execute();
    }
}
