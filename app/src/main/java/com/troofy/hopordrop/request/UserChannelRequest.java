package com.troofy.hopordrop.request;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshat666
 */
public class UserChannelRequest extends GoogleHttpClientSpiceRequest<List> {

    private  Context context;
    private long authID;
    private String token;
    private static final String TAG = "UserChannelRequest";

    public UserChannelRequest(long authID, String token,Context context){
        super(List.class);
        this.authID = authID;
        this.token = token;
        this.context = context;
        this.authID =authID;
    }

    @Override
    public List loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.GET_USER_CHANNEL_DETAILS)+"/"+authID;

        GenericUrl genericUrl = new GenericUrl(url);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));
        request.setConnectTimeout(30000);
        HttpResponse response = request.execute();

        List jsonArray = new ArrayList();
        try{

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONArray finalResult = new JSONArray(tokener);

        if (finalResult != null) {
            for (int i=0;i<finalResult.length();i++){
                jsonArray.add(finalResult.optJSONObject(i));
            }
        }

        }catch (IOException io){
            Log.e(TAG,"IOException in request data :"+io.getMessage());
        }catch (JSONException je){
            Log.e(TAG,"JSONException in request data :"+je.getMessage());
        }


        return jsonArray;
    }
}
