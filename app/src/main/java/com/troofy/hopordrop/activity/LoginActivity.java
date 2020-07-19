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

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.FBLoginBean;
import com.troofy.hopordrop.bean.UserCurrentStatusBean;
import com.troofy.hopordrop.frag.FBLoginFragment;
import com.troofy.hopordrop.frag.NetworkDialogFragment;
import com.troofy.hopordrop.frag.SplashFragment;
import com.troofy.hopordrop.request.TokenUpdateRequest;
import com.troofy.hopordrop.request.UserCurrentStateRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;


public class LoginActivity extends SpiceBaseActivity implements
        NetworkDialogFragment.NetworkDialogInterface {

    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private Context context;
    private ProgressDialog progressDialog;
    private SharedPreferences sPref;
    private boolean fbTokenUpdated = false;
    private String token;
    private long authID;
    public static final String splashFrag = "splashFrag";
    public static final String fbLoginFrag = "fbLoginFrag";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;
        setTitle(R.string.welcome);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (SystemUtils.isNetworkAvailable(this)) {

            SystemUtils.checkLocationService(this);

            FacebookSdk.sdkInitialize(this, new FacebookSdk.InitializeCallback() {
                @Override
                public void onInitialized() {
                    sPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
                    if (isUserLoggedIn()) {

                        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
                        SplashFragment splashFrag = new SplashFragment();
                        fragTransaction.replace(R.id.fbLoginLayout, splashFrag, LoginActivity.splashFrag).commit();

                        AccessToken.refreshCurrentAccessTokenAsync();
                        //progressDialog = ProgressDialog.show(context, getString(R.string.app_name), getString(R.string.wait), false, false);
                        defineAccessTokenTracker();

                    } else {
                        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
                        FBLoginFragment fbFrag = new FBLoginFragment();
                        fragTransaction.replace(R.id.fbLoginLayout, fbFrag, LoginActivity.fbLoginFrag).commit();

                    }
                }
            });


        } else {
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);

        }
        // ----------- Do not add anything after this line ---------------
        // The flow should be in defineAccessTokenTracker();


    }

    private void defineAccessTokenTracker() {

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                //progressDialog.dismiss();
                fbTokenUpdated = true;
                if (currentAccessToken == null) {
                    //Toast.makeText(context, R.string.detailsUpdatedLoginAgain, Toast.LENGTH_LONG).show();
//                    logOutUser();
                    //startActivity(new Intent(context, LoginActivity.class));
                    return;
                }

                FBLoginBean fbLoginBean = new FBLoginBean();
                fbLoginBean.setToken(currentAccessToken.getToken());
                fbLoginBean.setApplicationID(currentAccessToken.getApplicationId());
                fbLoginBean.setExpires(currentAccessToken.getExpires().getTime());
                fbLoginBean.setSource(currentAccessToken.getSource().name());
                fbLoginBean.setLastRefresh(currentAccessToken.getLastRefresh().getTime());
                fbLoginBean.setUserID(currentAccessToken.getUserId());


                long authID = sPref.getLong(getString(R.string.authIDStr), 0);
                if (authID <= 0) {
                    logOutUser();
                }

                Genson gen = new Genson();
                String json = gen.serialize(fbLoginBean);

                TokenUpdateRequest request = new TokenUpdateRequest(context, json, authID);

                //progressDialog = ProgressDialog.show(context, getString(R.string.app_name), getString(R.string.wait), false, false);
                spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new LoginActivity.TokenUpdateListener());

            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();

         if (SystemUtils.isNetworkAvailable(context)) {
            // Logs 'install' and 'app activate' App Events.
            AppEventsLogger.activateApp(this);
             if(!FacebookSdk.isInitialized())
             {
                 FacebookSdk.sdkInitialize(this);
             }

            if (isUserLoggedIn() && fbTokenUpdated) {
                Intent i = new Intent(this, PoolMainActivity.class);
                startActivity(i);
            }

        } else {
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void tryAgain() {
        startActivity(new Intent(this, LoginActivity.class));
    }


    public void logOutUser() {
        if(!FacebookSdk.isInitialized())
        {
            FacebookSdk.sdkInitialize(this);
        }
        LoginManager.getInstance().logOut();
        SystemUtils.clearSharedPref(this);
        return;
    }

    public boolean isUserLoggedIn() {

        accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && SystemUtils.isUserDetailedSaved(this);
    }


    private class TokenUpdateListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context, R.string.detailsUpdatedLoginAgain, Toast.LENGTH_LONG);
            logOutUser();
            Intent i = new Intent(context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onRequestSuccess(HttpResponse httpResponse) {
            fetchUserState();
        }
    }

    public void fetchUserState(){
        //Load splash screen if not loaded

        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
        SplashFragment frag = (SplashFragment) fragMan.findFragmentByTag(LoginActivity.splashFrag);
        if(!(frag != null && frag.isVisible())){
            android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
            SplashFragment splashFrag = new SplashFragment();
            fragTransaction.replace(R.id.fbLoginLayout, splashFrag, LoginActivity.splashFrag).commit();
        }


        //call to server to fetch latest status - trip running? PickupReq running?
        authID = sPref.getLong("authID", 0);
        token = sPref.getString("token", null);
        UserCurrentStateRequest request = new UserCurrentStateRequest(authID, token ,context);
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new UserCurrentStateListener());
    }

    private class UserCurrentStateListener implements com.octo.android.robospice.request.listener.RequestListener<com.troofy.hopordrop.bean.UserCurrentStatusBean> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context, R.string.err_generic_msg, Toast.LENGTH_LONG);
            logOutUser();
            Intent i = new Intent(context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onRequestSuccess(UserCurrentStatusBean currentStatusBean) {

            if(currentStatusBean != null){

                if(currentStatusBean.getUserStatus() > 0 && currentStatusBean.getUserStatus() != AppConstants.STATUS_AUTH_USER_ACTIVE){
                    Toast.makeText(context, R.string.user_flag_support, Toast.LENGTH_LONG);
                    logOutUser();
                    Intent i = new Intent(context, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    return;
                }

                if(currentStatusBean.getTripStatus() == AppConstants.STATUS_TRIP_CREATED){
                    sPref.edit().putLong(context.getString(R.string.tripIdStr),currentStatusBean.getTripID()).commit();
                }
                else if(currentStatusBean.getPickReqStatus() == AppConstants.STATUS_PICKREQ_NEW){
                    sPref.edit().putLong(context.getString(R.string.pickIdStr),currentStatusBean.getPickReqID()).commit();
                }

            }

            Intent i = new Intent(context, PoolMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }
    }


}
