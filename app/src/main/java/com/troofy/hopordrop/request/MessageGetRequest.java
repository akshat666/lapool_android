package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

import java.util.List;

/**
 * Created by akshat666
 */
public class MessageGetRequest extends GoogleHttpClientSpiceRequest<List>{

    private long tripID;
    private long msgID;
    private long chatAuthID;
    private String token;
    private Context context;
    private long authID;

    public MessageGetRequest(long tripID, long msgID, long chatAuthID, String token, long authID, Context context ) {
        super(List.class);
        this.tripID = tripID;
        this.token = token;
        this.msgID = msgID;
        this.chatAuthID = chatAuthID;
        this.context = context;
        this.authID = authID;
    }

    @Override
    public List loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.GET_MSG);

        GenericUrl genericUrl = new GenericUrl(url);
        genericUrl.put("tripID",this.tripID);
        genericUrl.put("msgID",this.msgID);
        genericUrl.put("chatAuthID",this.chatAuthID);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));
        request.setConnectTimeout(60000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());

        return request.execute().parseAs(List.class);
    }
}
