package com.troofy.hopordrop.request;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

/**
 * Created by akshat
 */
public class GoogleLocationSearchRequest extends GoogleHttpClientSpiceRequest<AutocompletePredictionBuffer> {

    private PendingResult result;

    public GoogleLocationSearchRequest(PendingResult result){
        super(AutocompletePredictionBuffer.class);
        this.result = result;
    }


    @Override
    public AutocompletePredictionBuffer loadDataFromNetwork() throws Exception {

        AutocompletePredictionBuffer autocompletePredictions = (AutocompletePredictionBuffer) result.await();
        if(autocompletePredictions.getStatus().isSuccess()){
            return autocompletePredictions;
        }
        else
        {
            String msg = autocompletePredictions.getStatus().getStatusMessage();
            autocompletePredictions.release();
            throw new Exception(msg);

        }


    }
}
