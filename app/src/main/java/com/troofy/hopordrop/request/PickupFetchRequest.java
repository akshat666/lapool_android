package com.troofy.hopordrop.request;

import android.content.Context;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.bean.UserCurrentStatusBean;

/**
 * Created by akshat666
 */
public class PickupFetchRequest extends GoogleHttpClientSpiceRequest<PickUpRequestBean> {

    private Context context;
    private String token;
    private long pickReqID;
    private long authID;

    public PickupFetchRequest(Context context, String token, long authID, long pickReqID){
        super(PickUpRequestBean.class);
        this.pickReqID = pickReqID;
        this.token = token;
        this.authID = authID;
        this.context = context;
    }

    @Override
    public PickUpRequestBean loadDataFromNetwork() throws Exception {
        String url = context.getString(R.string.BASE_SERVICE_URL) + context.getString(R.string.PICKREQ_REQUEST) + "/" + pickReqID;

        GenericUrl genericUrl = new GenericUrl(url);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(genericUrl).setHeaders(new HttpHeaders().set("token",this.token).set("authID",authID));

        request.setHeaders((new HttpHeaders()).set("token", token).set("authID",authID));
        request.setParser(new com.google.api.client.json.jackson2.JacksonFactory().createJsonObjectParser());
        request.setConnectTimeout(60000);

        HttpResponse response = request.execute();
        return response.parseAs(PickUpRequestBean.class);
    }
}
