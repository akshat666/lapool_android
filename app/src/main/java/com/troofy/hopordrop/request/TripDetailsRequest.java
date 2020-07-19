package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by akshat666
 */
public class TripDetailsRequest extends GoogleHttpClientSpiceRequest<JSONObject> {

    private long tripID;
    private Context context;
    private String token;
    private long authID;

    public TripDetailsRequest(long tripID, Context context, String token, long authID){
        super(JSONObject.class);
        this.tripID = tripID;
        this.context = context;
        this.token = token;
        this.authID=authID;
    }
    @Override
    public JSONObject loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.GET_TRIP_DETAILS)+"/"+tripID;
        GenericUrl genericUrl = new GenericUrl(url);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));
        request.setConnectTimeout(30000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());

        HttpResponse response = request.execute();

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONObject jsonObject = new JSONObject(tokener);
        return jsonObject;
//        return request.execute().parseAs(JSONObject.class);
    }
}
