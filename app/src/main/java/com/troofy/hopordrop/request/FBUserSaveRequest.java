package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.UserBean;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by akshat
 */
public class FBUserSaveRequest extends GoogleHttpClientSpiceRequest<UserBean> {

    private String fbLoginBeanJson;
    private Context context;

    public FBUserSaveRequest(String fbLoginBeanJson, Context context) {
        super(UserBean.class);
        this.fbLoginBeanJson = fbLoginBeanJson;
        this.context = context;
    }

    @Override
    public UserBean loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.USER_FB_LOGIN_SAVE_URL);

        HttpRequest request = getHttpRequestFactory()//
                .buildPostRequest(new GenericUrl(url), ByteArrayContent.fromString("application/json", fbLoginBeanJson));
        request.setConnectTimeout(60000);
        HttpResponse response = request.execute();

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        JSONObject jsonObject = new JSONObject(tokener);
        UserBean userBean = new UserBean();
        userBean.setAuthID(jsonObject.getLong("authID"));
        userBean.setToken(jsonObject.getString("token"));
        return userBean;
    }
}
