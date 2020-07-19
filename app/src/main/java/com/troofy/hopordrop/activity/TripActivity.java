package com.troofy.hopordrop.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.AlertBean;
import com.troofy.hopordrop.bean.ChangeStatusBean;
import com.troofy.hopordrop.frag.NetworkDialogFragment;
import com.troofy.hopordrop.receiver.AlarmReceiver;
import com.troofy.hopordrop.request.AlertRequest;
import com.troofy.hopordrop.request.TripDetailsRequest;
import com.troofy.hopordrop.request.TripEndRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TripActivity extends LocationActivity implements OnMapReadyCallback, NetworkDialogFragment.NetworkDialogInterface{

    private static final String TAG = "TripActivity";
    private static final int PICK_CONTACT_1 = 1;

    private Location currentLocation;
    private MapFragment map;
    private long tripID = 0;
    private JSONObject tripDetailsJson = null;
    private String token;
    private long authID;
    private GoogleMap googleMap;
    private ApplicationVariables appVariables;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private Context context;
    private long hopperAuthID;
    private long dropperAuthID;
    private SharedPreferences defaultSharedPref;
    private String hopperGender;
    private String dropperGender;
    private int hopperAge;
    private int dropperAge;
    private String hopperProviderID;
    private String dropperProviderID;
    private ProgressDialog progressDialog;
    private TextView nameFld;
    private TextView ageGenderFld;
    private ProfilePictureView thumbNail;
    private long chatAuthId;
    private String chatName;
    private boolean isDataFetching = false;
    private String tripKey;
    //private Animation animationBlink;
    private String alertKey;
    private ImageView sosImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        this.context = this;

        if (!SystemUtils.isNetworkAvailable(this)) {
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.setCancelable(false);
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);
        }

        SystemUtils.checkLocationService(this);

        appVariables = (ApplicationVariables) this.getApplicationContext();

        defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        this.token = defaultSharedPref.getString("token", null);
        this.authID = defaultSharedPref.getLong("authID", 0);
        this.tripID = defaultSharedPref.getLong(getString(R.string.tripIdStr), 0);

        //If trip is over
        if (tripID == 0) {
            Intent i = new Intent(TripActivity.this, ErrorActivity.class);
            startActivity(i);
            finish();
        }

        //Start sending trip location updates
        startTripLocationService();

        isDataFetching = true;
        fetchTripDetails();

        nameFld = (TextView) findViewById(R.id.nameFldTrip);

        ageGenderFld = (TextView) findViewById(R.id.ageGenderFld);

        thumbNail = (ProfilePictureView) findViewById(R.id.thumbnail);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.tripMap));
        map.setRetainInstance(true);
        map.getMapAsync(this);

        Button endTripBtn = (Button) findViewById(R.id.endTripBtn);
        endTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Do you want to end the trip?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                endTrip();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        ImageView alertBtn = (ImageView) findViewById(R.id.alertBtn);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.emgAlert)
                        .setMessage(R.string.sendSOS)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendAlert();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        ImageView trackImg = (ImageView) findViewById(R.id.trackImg);
        trackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Share trip details!")
                        .setMessage("Share your trip and location updates in real time to family and friends?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                startTracking();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        final ImageView chatImg = (ImageView) findViewById(R.id.chatImg);
        chatImg.setClickable(true);
        chatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("chatAuthID", chatAuthId);
                i.putExtra("chatName", chatName);
                startActivity(i);
            }
        });

    }

    /**
     * Starts an alarm service for updating trip locations
     */
    private void startTripLocationService() {
        //If alarm is not set - set an alarm for trip locations
//        if (!defaultSharedPref.getBoolean(getString(R.string.tripLocationPoll), false)) {
            //Set an alarmManager for pinging location
            //If no alarm is set
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.putExtra(getString(R.string.tripLocationPoll), true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AppConstants.TRIP_LOC_ALARM_ID, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AppConstants.tripPollingInterval,
                    pendingIntent);
            defaultSharedPref.edit().putBoolean(getString(R.string.tripLocationPoll), true).apply();
//        }
    }

    private void sendAlert() {
        registerAlert();
    }

    private void sendSMS(String num, String key) {
        try {
            SystemUtils.sendSMS(num, getString(R.string.alertSMSTxt)+getString(R.string.ALERT_URL)+"?q="+key, this);
            Toast.makeText(getApplicationContext(), R.string.alertSent,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    R.string.smsFail,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

//    private void animateAlertButton() {
//        //Animate the alert button and show text
//        // load the animation
//        animationBlink = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.blink_animation);
//        sosImg = (ImageView)findViewById(R.id.alertBtn);
//        sosImg.startAnimation(animationBlink);
//
//    }

    private void registerAlert() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        AlertBean alertBean = new AlertBean();
        alertBean.setTripID(this.tripID);
        alertBean.setAuthID(this.authID);
        alertBean.setLat(mLastLocation.getLatitude());
        alertBean.setLng(mLastLocation.getLongitude());
        Genson gen  = new Genson();
        String json = gen.serialize(alertBean);

        AlertRequest alertRequest = new AlertRequest(this, this.authID,this.token, json);
        spiceManager.execute(alertRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new AlertRequestListener());

    }


    private void fetchTripDetails() {
        //progressDialog = ProgressDialog.show(this,getString(R.string.processTrip),getString(R.string.wait),false,false);

        TripDetailsRequest tripReq = new TripDetailsRequest(this.tripID, this, token, authID);
        spiceManager.execute(tripReq, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new TripDetailsRequestListener());
    }


    private void endTrip() {

        progressDialog = ProgressDialog.show(this,getString(R.string.endingTrip),getString(R.string.wait),false,false);

        ChangeStatusBean changeStatusBean = new ChangeStatusBean();
        changeStatusBean.setAuthID(this.authID);
        changeStatusBean.setId(this.tripID);
        if (hopperAuthID == authID) {
            changeStatusBean.setStatus(AppConstants.STATUS_TRIP_ENDED_BY_HOPPER);
        } else {
            changeStatusBean.setStatus(AppConstants.STATUS_TRIP_ENDED_BY_DROPPER);
        }

        Genson gen = new Genson();
        String json = gen.serialize(changeStatusBean);

        TripEndRequest tripEndRequest = new TripEndRequest(json, this, token, authID);
        spiceManager.execute(tripEndRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new TripEndRequestListener());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        buildGoogleApiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        if (mLastLocation == null) {
            mLastLocation = appVariables.getCurrentLocation();
        }

        //Clear previous markers in cache
        for (Marker mark : markerList) {
            mark.remove();
        }
        googleMap.clear();
        markerList.clear();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    @Override
    public void tryAgain() {
        startActivity(new Intent(this, TripActivity.class));
    }

//    @Override
//    public void logOutUser() {
//        Toast.makeText(context, "You cannot logout right now!", Toast.LENGTH_SHORT).show();
//
//        return;
//    }


    public void startTracking() {

        try
        { Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.shareTrackTrip);
            sAux = sAux + getString(R.string.trackTxt)+getString(R.string.TRACK_URL)+"?j="+tripKey;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, getString(R.string.share_using)));
        }
        catch(Exception e)
        {
        }

    }

    private class TripDetailsRequestListener implements com.octo.android.robospice.request.listener.RequestListener<JSONObject> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //progressDialog.dismiss();
            isDataFetching = false;
        }

        @Override
        public void onRequestSuccess(JSONObject tripJson) {
            //progressDialog.dismiss();
            tripDetailsJson = tripJson;

            try {
                int status = tripDetailsJson.getInt("status");
                //If trip has already ended
                if (status <= 0 || status == AppConstants.STATUS_TRIP_ENDED_BY_DROPPER || status == AppConstants.STATUS_TRIP_ENDED_BY_HOPPER) {
                    SystemUtils.stopTripLocationService(context);
                    clearTripSavedValues();
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(context, getString(R.string.tripEnded), Toast.LENGTH_SHORT).show();
                    finish();
                }

                double dropperLat = tripDetailsJson.getDouble("dropperStartLat");
                double dropperLng = tripDetailsJson.getDouble("dropperStartLng");
                double dropLat = tripDetailsJson.getDouble("dropLat");
                double dropLng = tripDetailsJson.getDouble("dropLng");
                double pickLat = tripDetailsJson.getDouble("pickupLat");
                double pickLng = tripDetailsJson.getDouble("pickupLng");
                String dropperName = tripDetailsJson.getString("dropperName");
                String hopperName = tripDetailsJson.getString("hopperName");
                hopperGender = tripDetailsJson.getString("hopperSex");
                dropperGender = tripDetailsJson.getString("dropperSex");
                hopperAge = tripDetailsJson.getInt("hopperAge");
                dropperAge = tripDetailsJson.getInt("dropperAge");
                dropperProviderID = tripDetailsJson.getString("dropperProviderID");
                hopperProviderID = tripDetailsJson.getString("hopperProviderID");

                hopperAuthID = tripDetailsJson.getLong("hopperID");
                dropperAuthID = tripDetailsJson.getLong("dropperID");

                tripKey = tripDetailsJson.getString(getString(R.string.tripKeyStr));

                if (hopperAuthID == authID) {

                    nameFld.setText(dropperName);
                    ageGenderFld.setText("" + SystemUtils.returnAgeGroup(dropperAge) + " yrs (" + (dropperGender.equalsIgnoreCase("m") ? getString(R.string.male) : getString(R.string.female)) + ")");
                    thumbNail.setProfileId(dropperProviderID);
                    chatName = dropperName;
                    chatAuthId = dropperAuthID;
                } else {
                    nameFld.setText(hopperName);
                    ageGenderFld.setText(SystemUtils.returnAgeGroup(hopperAge) + " yrs (" + (hopperGender.equalsIgnoreCase("m") ? getString(R.string.male) : getString(R.string.female)) + ")");
                    thumbNail.setProfileId(hopperProviderID);
                    chatName = hopperName;
                    chatAuthId = hopperAuthID;

                }
                defaultSharedPref.edit().putLong(context.getString(R.string.chatAuthIDStr), chatAuthId).apply();
                defaultSharedPref.edit().putString(context.getString(R.string.chatNameStr), chatName).apply();
                defaultSharedPref.edit().putString(context.getString(R.string.tripKeyStr), chatName).apply();

                LatLng pickLatLng = new LatLng(pickLat, pickLng);
                LatLng dropLatLng = new LatLng(dropLat, dropLng);
                LatLng dropperLatLng = new LatLng(dropperLat, dropperLng);

                MarkerOptions dropperMarker = new MarkerOptions();
                dropperMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.letter_d));
                dropperMarker.position(dropperLatLng);
                dropperMarker.title(dropperName);

                markerList.add(googleMap.addMarker(dropperMarker));

                MarkerOptions pickMarker = new MarkerOptions();
                pickMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.letter_h));
                pickMarker.position(pickLatLng);
                pickMarker.title(hopperName);
                markerList.add(googleMap.addMarker(pickMarker));

                MarkerOptions destinationMarker = new MarkerOptions();
                destinationMarker.position(dropLatLng);
                destinationMarker.title(getString(R.string.destination));
                markerList.add(googleMap.addMarker(destinationMarker));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(dropperLatLng);
                builder.include(pickLatLng);
                builder.include(dropLatLng);
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60));



            } catch (JSONException e) {
                e.printStackTrace();
            }
            isDataFetching = false;

        }
    }

    private class TripEndRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            Toast.makeText(context, R.string.trip_ending_err, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(HttpResponse response) {
            progressDialog.dismiss();

            clearTripSavedValues();
            SystemUtils.stopTripLocationService(context);

            Intent i = new Intent(context, PoolMainActivity.class);
            startActivity(i);
            Toast.makeText(context, R.string.tripEnded, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void clearTripSavedValues() {
        SystemUtils.clearTripParameters(this);
    }



    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemUtils.isNetworkAvailable(this)) {
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);
        }

        if (!isDataFetching) {

            //If trip is over
            if (tripID == 0) {
                Intent i = new Intent(TripActivity.this, ErrorActivity.class);
                startActivity(i);
                finish();
            }
            isDataFetching = true;
            defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
            this.tripID = defaultSharedPref.getLong(getString(R.string.tripIdStr), 0);

            fetchTripDetails();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_CONTACT_1){
            if(resultCode == Activity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  this.getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sendSMS(number,this.alertKey);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isDataFetching = false;
    }

    private class AlertRequestListener implements com.octo.android.robospice.request.listener.RequestListener<AlertBean> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Alert Failed!");
            alert.setMessage("Please call the police/authorities immediately");
            alert.setPositiveButton("OK",null);
            alert.show();


        }

        @Override
        public void onRequestSuccess(AlertBean alertBean) {

            defaultSharedPref.edit().putLong(getString(R.string.alertID), alertBean.getAlertID()).apply();

            alertKey = alertBean.getKey();
            ArrayList<String> contactNos = new ArrayList<>();

            contactNos.add(defaultSharedPref.getString(getString(R.string.contactnumber1), null));
            contactNos.add(defaultSharedPref.getString(getString(R.string.contactnumber2), null));

            if (contactNos.isEmpty()) {
                //select one contact at least
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, PICK_CONTACT_1);

            }else{
                //Send url to emergency contacts
                for(String num: contactNos){
                    sendSMS(num, alertKey);
                }
            }
//            animateAlertButton();
            startAlertLocationPooling();

            TextView sosTxt = (TextView) findViewById(R.id.SOSTxt);
            sosTxt.setVisibility(View.VISIBLE);
        }
    }

    private void startAlertLocationPooling() {

//        //If alarm is not set - set an alarm for alert locations
//        if (!defaultSharedPref.getBoolean(getString(R.string.alertLocationPoll), false)) {
            //Set an alarmManager for pinging location
            //If no alarm is set
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.putExtra(getString(R.string.alertLocationPoll), true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AppConstants.ALERT_ALARM_ID, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AppConstants.sosPollingInterval,
                    pendingIntent);
            defaultSharedPref.edit().putBoolean(getString(R.string.alertLocationPoll), true).apply();
        }
//
//    }
}
