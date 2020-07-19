/*
 * ************************************************************************
 *  *
 *  * TROOFY LABS - CONFIDENTIAL
 *  * __________________
 *  *  Copyright (c) 2016.
 *  *
 *  *  All Rights Reserved.
 *  *
 *  * NOTICE:  All information contained herein is, and remains
 *  * the property of Troofy Labs(OPC) Private Limited and its suppliers,
 *  * if any.  The intellectual and technical concepts contained
 *  * herein are proprietary to Troofy Labs(OPC) Private Limited
 *  * and its suppliers and may be covered by U.S. and Foreign Patents,
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * from Troofy Labs(OPC) Private Limited.
 *
 */

package com.troofy.hopordrop.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.squareup.picasso.Picasso;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.MutualFriendsBean;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.request.ConfirmTripRequest;
import com.troofy.hopordrop.request.MutualConnectionRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PickUpConfirmationActivity extends LocationActivity {

    private static final String TAG = "PickConfrmActivity";
    private PickUpRequestBean pickUpRequest;
    private Context context;
    private ProgressDialog progressDialog;
    private SharedPreferences defaultSharedPref;
    private long authID;
    private String token;
    private GridLayout mutualFndLayout;
    private TextView mutualTitle;
    private TextView loadingMutualTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        setContentView(R.layout.activity_pick_details);

        this.context = this;
        pickUpRequest = (PickUpRequestBean)getIntent().getSerializableExtra("pickUpRequest");
        //pickUpRequest.getAuthID();

        mutualFndLayout = (GridLayout) findViewById(R.id.mutualFndLayout);

        defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        authID = defaultSharedPref.getLong("authID", 0);
        token = defaultSharedPref.getString("token", null);

        fetchMutualConnections(authID,token, pickUpRequest.getAuthID());

        ProfilePictureView thumbNail = (ProfilePictureView)findViewById(R.id.thumbnail);
        thumbNail.setProfileId(pickUpRequest.getUserID());
        TextView name = (TextView)findViewById(R.id.userName_2);
        name.setText(pickUpRequest.getName().toUpperCase());

        TextView sex = (TextView)findViewById(R.id.sexTxt_2);
        //Sex
        if(pickUpRequest.getGender() == 'm'){
            sex.setText(getResources().getString(R.string.male));
        }
        else{
            sex.setText(getResources().getString(R.string.female));
        }

        TextView age = (TextView)findViewById(R.id.ageTxt_2);
        age.setText(SystemUtils.returnAgeGroup(pickUpRequest.getAge())+ " Yrs");

        TextView pick = (TextView)findViewById(R.id.pickupTxt_2);
        pick.setText(pickUpRequest.getPickAddress().toUpperCase());

        TextView drop = (TextView)findViewById(R.id.dropTxt_2);
        drop.setText(pickUpRequest.getDropAddress().toUpperCase());

        TextView pickTimeTxt = (TextView)findViewById(R.id.pickReqTimeID);
        pickTimeTxt.setText(new SimpleDateFormat(getString(R.string.dateTimeFormat)).format(pickUpRequest.getCreated()));

        if(!pickUpRequest.isMusic()){
            ImageView musicImg = (ImageView)findViewById(R.id.musicImg);
            musicImg.setAlpha(0.2f);
        }
        if(!pickUpRequest.isSmoking()){
            ImageView smokeImg = (ImageView)findViewById(R.id.smokeImg);
            smokeImg.setAlpha(0.2f);
            smokeImg.setImageResource(R.drawable.ic_smoke_free_black_48dp);
        }

        ImageView seatsImg = (ImageView)findViewById(R.id.seatsImg);
        switch (pickUpRequest.getSeats()){
            case 1:
                seatsImg.setImageResource(R.drawable.oneseat);
                break;
            case 2:
                seatsImg.setImageResource(R.drawable.twoseat);
                break;
            case 3:
                seatsImg.setImageResource(R.drawable.threeseat);
                break;
        }


        //Linked networks
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linkedNetworksInDetails);
        for(Integer nw : (List<Integer>)pickUpRequest.getNetworkLinked()){
            if(nw == AppConstants.NETWORK_FACEBOOK){
                TextView fbTxt = new TextView(context);
                fbTxt.setText(R.string.fb);
                fbTxt.setTypeface(fbTxt.getTypeface(), Typeface.BOLD);
                fbTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                linearLayout.addView(fbTxt);
            }else if(nw == AppConstants.NETWORK_WORK){
                ImageView workImg = new ImageView(context);
                workImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_work_black_24dp));
                workImg.setMaxHeight(125);
                workImg.setMaxWidth(125);
                workImg.setMinimumHeight(125);
                workImg.setMinimumWidth(125);
                linearLayout.addView(workImg);
            }else{
                TextView nmTxt = new TextView(context);
                nmTxt.setText(R.string.public_brac);
                nmTxt.setTypeface(nmTxt.getTypeface(), Typeface.BOLD);
                nmTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                linearLayout.addView(nmTxt);
            }
        }

        final Button confirm = (Button) findViewById(R.id.confirmTrip);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(false);
                confirmTripToServer(authID,token);
            }
        });

        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setIndeterminate(true);
        ProgressBar progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar3.setIndeterminate(true);
        ProgressBar progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar4.setIndeterminate(true);

        mutualTitle = (TextView)findViewById(R.id.mutualTitle);



    }

    private void fetchMutualConnections(long authID, String token, long userInReqAuthID) {

        loadingMutualTxt = new TextView(context);
        loadingMutualTxt.setText(getString(R.string.load_mutual_friends));
        loadingMutualTxt.setTextColor(ContextCompat.getColor(context,R.color.black));
        mutualFndLayout.addView(loadingMutualTxt);

        MutualConnectionRequest request = new MutualConnectionRequest(authID, token, userInReqAuthID, context);
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new MutualConnectionListener());
    }

    private void confirmTripToServer(long authID, String token) {
        progressDialog = ProgressDialog.show(this,getString(R.string.app_name),getString(R.string.wait),false,false);
        ConfirmTripRequest request = new ConfirmTripRequest(pickUpRequest.getPickID(),authID,
                context,token,mLastLocation.getLatitude(),mLastLocation.getLongitude());
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new ConfirmPickUpRequestListener());

    }

    private class ConfirmPickUpRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //To to fail view
            progressDialog.dismiss();
            Intent i = new Intent(context,ErrorActivity.class);
            startActivity(i);
            finish();
            return;
        }

        @Override
        public void onRequestSuccess(HttpResponse o) {
            progressDialog.dismiss();
            if(o.getStatusCode() == AppConstants.HTTP_CODE_INTERNAL_ERROR_500){
                //Navigate to error view
                Intent i = new Intent(context,ErrorActivity.class);
                startActivity(i);
                finish();
            }
            //Go to running trip view
            long tripID = 0;
            try {
                tripID = o.parseAs(Long.class);
                defaultSharedPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
                defaultSharedPref.edit().putLong(context.getString(R.string.tripIdStr),tripID).commit();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(context, TripActivity.class);
            i.putExtra("tripID",tripID);
            startActivity(i);
            finish();
            return;
        }
    }

    private class MutualConnectionListener implements com.octo.android.robospice.request.listener.RequestListener<MutualFriendsBean> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mutualFndLayout.removeView(loadingMutualTxt);
            TextView noFriends = new TextView(context);
            noFriends.setText(getString(R.string.err_generic_msg));
            noFriends.setTextColor(ContextCompat.getColor(context,R.color.black));
            mutualFndLayout.addView(noFriends);
        }

        @Override
        public void onRequestSuccess(MutualFriendsBean mutualFriendsBean) {

            if(mutualFriendsBean == null || mutualFriendsBean.getUser().size() <= 0){
                mutualFndLayout.removeView(loadingMutualTxt);
                mutualTitle.append("(0)");
                TextView noFriends = new TextView(context);
                noFriends.setText(getString(R.string.no_mutual_friends));
                noFriends.setTextColor(ContextCompat.getColor(context,R.color.black));
                mutualFndLayout.addView(noFriends);
            }else{
                mutualFndLayout.removeView(loadingMutualTxt);
                mutualTitle.append("("+mutualFriendsBean.getTotalMutualFriends()+")");

                LayoutInflater li = LayoutInflater.from(context);

                for(MutualFriendsBean.User user:mutualFriendsBean.getUser()){
                    LinearLayout layout = (LinearLayout) li.inflate(R.layout.user_name_pic,null);
                    ImageView dp= (ImageView) layout.findViewById(R.id.user_pic);
                    Picasso.with(context).load(user.picUrl).into(dp);
                    TextView name = (TextView)layout.findViewById(R.id.user_name);

                    name.setTextColor(ContextCompat.getColor(context,R.color.black));
                    name.setEllipsize(TextUtils.TruncateAt.END);
                    name.setSingleLine(true);
                    name.setMaxLines(1);
                    name.setText(user.name);
                    mutualFndLayout.addView(layout);
                }

            }


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(context,GiveDropActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }
}
