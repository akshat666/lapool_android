package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
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
public class CheckUserInSystemRequest extends GoogleHttpClientSpiceRequest<JSONObject> {

    private String json;
    private Context context;

    public CheckUserInSystemRequest(String json, Context context){
        super(JSONObject.class);
        this.json = json;
        this.context = context;
    }

    @Override
    public JSONObject loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.CHECK_USER_IN_SYSTEM);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", json));

        request.setConnectTimeout(60000);
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());

        HttpResponse response = request.execute();
        JSONObject jsonObject = null;
        JSONTokener tokener = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null; ) {
            builder.append(line).append("\n");
        }
        tokener = new JSONTokener(builder.toString());
        jsonObject = new JSONObject(tokener);


        return jsonObject;
    }
}
