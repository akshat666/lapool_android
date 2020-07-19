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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.ChangeStatusBean;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.frag.InviteDialog;
import com.troofy.hopordrop.frag.NetworkDialogFragment;
import com.troofy.hopordrop.request.PickupFetchRequest;
import com.troofy.hopordrop.request.PickupReqCancelRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;
import com.wang.avi.AVLoadingIndicatorView;

public class PickUpDetailsActivity extends LocationActivity implements OnMapReadyCallback {

    private static final String TAG = "PickUpDetailsActivity";

    private long pickReqID;
    private SharedPreferences sPref;
    private Context context;
    private SupportMapFragment map;
    private GoogleMap googleMap;
    private LatLng dropPoint;
    private LatLng pickPoint;
    private ProgressDialog progressDialog;
    private Button cancelHopBtn;
    private long authID;
    private String token;
    private boolean isDataFetching = false;
    private long notifedUsers = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_details);


        if(!SystemUtils.isNetworkAvailable(this)){
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);
        }

        SystemUtils.checkLocationService(this);

        checkIfTripIsOn();

        //Check if pickupRequest is already running and stored in shared preference
        sPref = getSharedPreferences(getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        this.pickReqID = sPref.getLong(getString(R.string.pickIdStr), 0);
        authID = sPref.getLong("authID", 0);
        token = sPref.getString("token", null);
        Intent i = getIntent();
        notifedUsers = i.getLongExtra("notifiedUsers",0);

        //Check if pickup req is expired i.e. more than 15mins
//        if (!SystemUtils.isPickupReqRunning(this) && !SystemUtils.isTripRunning(this)) {
//            requestExpired();
//        }

//        if (this.pickReqID != 0) {
//            double dropLat = Double.valueOf(sPref.getString(getString(R.string.dropLatStr), null));
//            double dropLng = Double.valueOf(sPref.getString(getString(R.string.dropLngStr), null));
//            double pickLat = Double.valueOf(sPref.getString(getString(R.string.pickLatStr), null));
//            double pickLng = Double.valueOf(sPref.getString(getString(R.string.pickLngStr), null));
//            pickPoint = new LatLng(pickLat, pickLng);
//            dropPoint = new LatLng(dropLat, dropLng);
//        }

        this.context = this;

        //Fetch latest from server
        checkPickRequestStatus();

    }

    private void checkPickRequestStatus() {
        isDataFetching = true;
        progressDialog = ProgressDialog.show(this,getString(R.string.app_name),getString(R.string.wait),false,false);

        PickupFetchRequest request = new PickupFetchRequest(this, token, authID, pickReqID);
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new PickUpRequestBeanListener());

    }

    private void requestExpired(){
        SystemUtils.clearPickupRequestParameters(this);
        Intent i = new Intent(this, LoginActivity.class);
        Toast.makeText(this, R.string.hopExpired, Toast.LENGTH_LONG).show();
        startActivity(i);
        finish();
    }

    private void cancelPickupRequest() {
        progressDialog = ProgressDialog.show(this,getString(R.string.cancelilng_hop),getString(R.string.wait),false,false);

        sPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        ChangeStatusBean pickupReqChangeStatusBean = new ChangeStatusBean();
        pickupReqChangeStatusBean.setAuthID(authID);
        pickupReqChangeStatusBean.setStatus(AppConstants.STATUS_PICKREQ_CANCELLED);
        pickupReqChangeStatusBean.setId(this.pickReqID);

        Genson genson = new Genson();
        String json = genson.serialize(pickupReqChangeStatusBean);

        PickupReqCancelRequest request = new PickupReqCancelRequest(json, token, authID,this);

        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new CancelPickUpRequestListener());

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        buildGoogleApiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        googleMap.clear();

        googleMap.addMarker(new MarkerOptions().position(dropPoint).title(context.getResources().getString(R.string.destination))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        MarkerOptions pickupMarker = new MarkerOptions();
        //pickupMarker.logo(BitmapDescriptorFactory.fromResource(R.drawable.letter_d));
        pickupMarker.position(pickPoint);
        pickupMarker.title(getString(R.string.pickup_point));
        googleMap.addMarker(pickupMarker);

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(dropPoint);
//        builder.include(pickPoint);
//        final LatLngBounds bounds = builder.build();

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickPoint, 13));
            }
        });

    }


    private class CancelPickUpRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            progressDialog = null;
            cancelHopBtn.setEnabled(true);
            Toast.makeText(context,
                    R.string.problem_cancel_hop, Toast.LENGTH_SHORT)
                    .show();


        }

        @Override
        public void onRequestSuccess(HttpResponse response) {
            progressDialog.dismiss();
            progressDialog = null;
            Toast.makeText(getBaseContext(),
                    R.string.hop_cancelled, Toast.LENGTH_SHORT)
                    .show();
            //clear sPref of pickID
            SharedPreferences.Editor editor = sPref.edit();
            editor.remove(getString(R.string.pickIdStr));
            editor.apply();
            Intent i = new Intent(context, PoolMainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void checkIfTripIsOn(){
        if(SystemUtils.isTripRunning(this)){
            SystemUtils.clearPickupRequestParameters(this);
            Intent i = new Intent(this, TripActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.pickReqID = sPref.getLong(getString(R.string.pickIdStr), 0);
        checkIfTripIsOn();
        if(!SystemUtils.isPickupReqRunning(this) && !SystemUtils.isTripRunning(this)){
            requestExpired();
        }
        if(!isDataFetching){
            checkPickRequestStatus();
        }
    }

    private class PickUpRequestBeanListener implements com.octo.android.robospice.request.listener.RequestListener<PickUpRequestBean> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            isDataFetching = false;
            progressDialog.dismiss();
            Intent i = new Intent(context, ErrorActivity.class);
            finish();
            startActivity(i);
        }

        @Override
        public void onRequestSuccess(PickUpRequestBean pickUpRequestBean) {
            isDataFetching = false;
            progressDialog.dismiss();

            if(pickUpRequestBean == null){
                requestExpired();
                return;
            }

            double dropLat = pickUpRequestBean.getDropLat();
            double dropLng = pickUpRequestBean.getDropLng();
            double pickLat = pickUpRequestBean.getPickUpLat();
            double pickLng = pickUpRequestBean.getPickUpLng();
            pickPoint = new LatLng(pickLat, pickLng);
            dropPoint = new LatLng(dropLat, dropLng);


            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.picReqMap));
            map.setRetainInstance(true);
            map.getMapAsync((PickUpDetailsActivity)context);

            cancelHopBtn = (Button) findViewById(R.id.cancelHopBtn);
            cancelHopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.confirmation)
                            .setMessage(R.string.cancel_hop)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    cancelHopBtn.setEnabled(false);
                                    cancelPickupRequest();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });


            if(notifedUsers <= 1){
                ImageView notifiedImg = (ImageView)((PickUpDetailsActivity) context).findViewById(R.id.notifiedUsersImg);
                notifiedImg.setVisibility(View.VISIBLE);
                notifiedImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openInviteDialog();
                    }
                });


            }

            AVLoadingIndicatorView avi = (AVLoadingIndicatorView)findViewById(R.id.avi);
            avi.show();
        }
    }

    public void openInviteDialog(){
        InviteDialog inviteDialog = new InviteDialog();
        inviteDialog.show(getFragmentManager(),"inviteDlg");
    }
}
