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

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.location.Address;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.HopResultBean;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.bean.PlaceAutocomplete;
import com.troofy.hopordrop.request.GeocoderRequest;
import com.troofy.hopordrop.request.GoogleLocationSearchRequest;
import com.troofy.hopordrop.request.PickUpRequest;
import com.troofy.hopordrop.util.SystemUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AskRideActivity extends LocationActivity implements OnMapReadyCallback {

    protected SpiceManager spiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
    private AutocompletePredictionBuffer autocompletePredictions;

    private static final String TAG = "AskRideActivity";

    private MapFragment map;
    private LatLng pickUpLatLong;
    private ApplicationVariables appVariables;
    private SearchView searchView;
    private Context context;
    private ArrayList<PlaceAutocomplete> suggestionList;
    private CursorAdapter suggestionAdapter;
    private TextView destTxt;
    private TextView pickTxt;
    private String dropPlaceID;
    private String pickAddress;
    private String dropAddress;
    private boolean music = false;
    private boolean girlsOnly = false;
    private boolean smoking = false;
    private GoogleMap googleMap;
    private SharedPreferences sPref;
    private String userGender;
    private LatLng dropLatLng;
    private boolean openToAll = false;
    private Vibrator vibrator;
    private ProgressDialog progressDialog;
    private int noOfSeats = 1;
    private Toast shortToast;
    private EditText searchTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_ride);

        this.context = this;
        shortToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        sPref = getSharedPreferences(getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        this.userGender = sPref.getString("gender", null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        map.getMapAsync(this);


        //View mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView();
        //View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById((Integer.parseInt("2")));

        this.pickTxt = (TextView) findViewById(R.id.pickTxt);
        this.destTxt = (TextView) findViewById(R.id.destTxt);

//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnMyLocation.getLayoutParams();
//        // position on right bottom
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        rlp.setMargins(0, 0, 30, 30);

        //final ImageView locImg = (ImageView) findViewById(R.id.locImg);
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);


        this.searchView = (SearchView) findViewById(R.id.destSearchView);
        searchView.setIconifiedByDefault(false);

        appVariables = (ApplicationVariables) getApplicationContext();

        suggestionAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        searchView.setSuggestionsAdapter(suggestionAdapter);

        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        final AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(15);
        autoComplete.setHintTextColor(ContextCompat.getColor(context,R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3)
                {
                    performGeoLocationSearch(newText);
                }
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(suggestionList.get(position).description, false);
                searchView.clearFocus();
                searchTxt.setSelection(0);
                destTxt.setText(suggestionList.get(position).description);
                dropPlaceID = suggestionList.get(position).placeId.toString();
                dropAddress = suggestionList.get(position).description.toString();
                return true;
            }
        });

        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        searchTxt = (EditText) searchView.findViewById(id);



        final ImageView girlsOnlyBtn = (ImageView) findViewById(R.id.girlsOnlyBtn);
        if (userGender.equalsIgnoreCase("male")) {
            girlsOnlyBtn.setVisibility(ImageView.GONE);
        }
        girlsOnlyBtn.setAlpha(0.3f);
        girlsOnlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userGender.equalsIgnoreCase("male")) {
                    shortToast.setText(R.string.only_women_pref);
                    shortToast.show();
                    return;
                }
                if (girlsOnly == true) {
                    girlsOnly = false;
                    girlsOnlyBtn.setAlpha(0.3f);
                } else {
                    girlsOnly = true;
                    vibrator.vibrate(50);
                    girlsOnlyBtn.setAlpha(1f);

                    shortToast.setText(R.string.visibleToWomen);
                    shortToast.show();
                }
            }
        });

        final ImageView musicBtn = (ImageView) findViewById(R.id.musicBtn);
        musicBtn.setAlpha(0.3f);
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (music == true) {
                    music = false;
                    musicBtn.setAlpha(0.3f);
                } else {
                    music = true;
                    vibrator.vibrate(50);
                    musicBtn.setAlpha(1f);
                }
            }

        });
        final ImageView smokeBtn = (ImageView) findViewById(R.id.smokingBtn);
        smokeBtn.setAlpha(0.3f);
        smokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smoking == false) {
                    smoking = true;
                    vibrator.vibrate(50);
                    smokeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_smoking_rooms_black_48dp));
                    smokeBtn.setAlpha(1f);
                } else {
                    smoking = false;
                    smokeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_smoke_free_black_48dp));
                    smokeBtn.setAlpha(0.3f);
                }
            }
        });

        final ImageView openToAllBtn = (ImageView) findViewById(R.id.publicReq);
        openToAllBtn.setAlpha(0.3f);
        openToAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openToAll == false) {
                    openToAll = true;
                    vibrator.vibrate(50);
                    openToAllBtn.setAlpha(1f);
                    shortToast.setText(R.string.openToPublic);
                    shortToast.show();
                } else {
                    openToAll = false;
                    openToAllBtn.setAlpha(0.3f);
                    shortToast.setText(R.string.reqOpenToNwOnly);
                    shortToast.show();
                }
            }
        });


        // The request button
        final Button reqBtn = (Button) findViewById(R.id.reqBtn);
        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != dropPlaceID && pickAddress != null) {
                    // Send pick-drop request to server
                    processPickupRequest();
                    reqBtn.setEnabled(false);
                } else {
                    shortToast.setText(R.string.enter_dest);
                    shortToast.show();
                    return;
                }
            }
        });

        //No of seats button
        final ImageView seatBtn = (ImageView) findViewById(R.id.noOfSeats);
        seatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(50);
                ImageView noOfSeatImg = (ImageView) v;

                switch (Integer.parseInt(v.getTag().toString())) {
                    case 1:
                        noOfSeats = 2;
                        v.setTag(2);
                        noOfSeatImg.setImageResource(R.drawable.twoseat);
                        shortToast.setText(context.getResources().getQuantityString(R.plurals.request,1)+" 2 "+context.getResources().getQuantityString(R.plurals.seats,2));
                        shortToast.show();
                        break;
                    case 2:
                        noOfSeats = 3;
                        v.setTag(3);
                        noOfSeatImg.setImageResource(R.drawable.threeseat);
                        shortToast.setText(context.getResources().getQuantityString(R.plurals.request,1)+" 3 "+context.getResources().getQuantityString(R.plurals.seats,3));
                        shortToast.show();
                        break;
                    case 3:
                        noOfSeats = 1;
                        v.setTag(1);
                        noOfSeatImg.setImageResource(R.drawable.oneseat);
                        shortToast.setText(context.getResources().getQuantityString(R.plurals.request,1)+" 1 "+context.getResources().getQuantityString(R.plurals.seats,1));
                        shortToast.show();
                        break;
                }
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        googleMap.setPadding(0, 180, 0, 180);
        //If map is panned or moved, update the pickUp location point
//        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                // Get the center of the Map.
//                pickUpLatLong = googleMap.getCameraPosition().target;
//                fetchAddressDetails(pickUpLatLong.latitude, pickUpLatLong.longitude);
//            }
//
//        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                // Get the center of the Map.
                pickUpLatLong = googleMap.getCameraPosition().target;
                fetchAddressDetails(pickUpLatLong.latitude, pickUpLatLong.longitude);

            }
        });

        buildGoogleApiClient();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AskRideActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }else{
            setUpLocationMap();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setUpLocationMap();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    shortToast.setText(R.string.location_perm_denied);
                    shortToast.show();
                    Intent i = new Intent(context,PoolMainActivity.class);
                    this.finish();
                    startActivity(i);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setUpLocationMap() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        ApplicationVariables appVariables = (ApplicationVariables) getApplicationContext();
        appVariables.setGoogleApiClient(mGoogleApiClient);

        if (mLastLocation != null) {
            fetchAddressDetails(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            //Set location to Application level
            appVariables.setCurrentLocation(mLastLocation);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                googleMap.setMyLocationEnabled(true);
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    //.bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Get the center of the Map.
            pickUpLatLong = googleMap.getCameraPosition().target;


        } else {
            Toast.makeText(this, "No Location detected! Enable location in settings.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(context,PoolMainActivity.class);
            this.finish();
            startActivity(i);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        if (null != autocompletePredictions && !autocompletePredictions.isClosed()) {
            autocompletePredictions.release();
        }
        super.onStop();
    }

    private void fetchAddressDetails(double lat, double lng) {

        //http://maps.googleapis.com/maps/api/geocode
        GeocoderRequest request = new GeocoderRequest(lat, lng, 1, context);
        request.setRetryPolicy(null);
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ONE_MINUTE, new GeocoderSpiceListener());

    }


    private void performGeoLocationSearch(String queryTxt) {

        LatLng latLng = new LatLng(appVariables.getCurrentLocation().getLatitude(), appVariables.getCurrentLocation().getLongitude());
        final LatLngBounds bound = SystemUtils.buildLatLngBound(latLng, 500);
        PendingResult result =
                Places.GeoDataApi.getAutocompletePredictions(appVariables.getGoogleApiClient(), queryTxt, bound, null);
        //TODO - cache the response
        GoogleLocationSearchRequest request = new GoogleLocationSearchRequest(result);
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ONE_WEEK, new GoogleLocationSearchListener());
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================

    public final class GoogleLocationSearchListener implements RequestListener<AutocompletePredictionBuffer> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {

        }

        @Override
        public void onRequestSuccess(AutocompletePredictionBuffer buffer) {
            autocompletePredictions = buffer;
            Iterator<AutocompletePrediction> iterator = buffer.iterator();
            suggestionList = new ArrayList<>(buffer.getCount());
            String[] columns = {
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
            };
            MatrixCursor cursor = new MatrixCursor(columns);
            int i = 0;
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                suggestionList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(null)));
                String[] tmp = {Integer.toString(i), prediction.getFullText(null).toString()};
                cursor.addRow(tmp);
                i++;
            }
            suggestionAdapter.swapCursor(cursor);
            buffer.release();

        }
    }

    public final class GeocoderSpiceListener implements RequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(getBaseContext(),
                    "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(context, ErrorActivity.class);
            startActivity(i);
            finish();

        }

        @Override
        public void onRequestSuccess(List list) {
            //TODO: this array is throwing out of bound exception
            if (!list.isEmpty()) {
                Address add = (Address) list.get(0);
                final String address = add.getAddressLine(0) + ", " + add.getAddressLine(1);
                pickTxt.setText(address);
                pickAddress = address;
            }

        }
    }

    private void processPickupRequest() {

        progressDialog = ProgressDialog.show(this,getString(R.string.preparing_hop),getString(R.string.wait),false,false);

        //Get more info from placeID
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, dropPlaceID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place dropPlace = places.get(0);
                            sendPickRequestToServer(dropPlace);
                        }
                        places.release();
                    }
                });
    }

    private void sendPickRequestToServer(Place dropPlace) {

        PickUpRequestBean pickUpRequestBean = new PickUpRequestBean();
        pickUpRequestBean.setPickUpLat(this.pickUpLatLong.latitude);
        pickUpRequestBean.setPickUpLng(this.pickUpLatLong.longitude);
        pickUpRequestBean.setDropLat(dropPlace.getLatLng().latitude);
        pickUpRequestBean.setDropLng(dropPlace.getLatLng().longitude);
        pickUpRequestBean.setPickAddress(this.pickAddress);
        pickUpRequestBean.setDropAddress(this.dropAddress);
        pickUpRequestBean.setGirlsOnly(girlsOnly);
        pickUpRequestBean.setSmoking(smoking);
        pickUpRequestBean.setMusic(music);
        pickUpRequestBean.setOpenToAll(openToAll);
        pickUpRequestBean.setSeats(noOfSeats);

        this.dropLatLng = dropPlace.getLatLng();

        sPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long authID = sPref.getLong("authID", 0);
        String userID = sPref.getString("userID", null);
        String token = sPref.getString("token", null);
        pickUpRequestBean.setUserID(userID);
        pickUpRequestBean.setAuthID(authID);

        Genson genson = new Genson();
        String pickUpReqJSON = genson.serialize(pickUpRequestBean);
        PickUpRequest request = new PickUpRequest(pickUpReqJSON, token, authID,this);
        request.setRetryPolicy(null);

        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new PickUpRequestListener());
    }

    public final class PickUpRequestListener implements RequestListener<HopResultBean> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();

            Toast.makeText(getBaseContext(),
                    "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(context, ErrorActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        public void onRequestSuccess(HopResultBean resultBean) {
            progressDialog.dismiss();

            //Set pref with req details
            if (sPref == null) {
                sPref = getSharedPreferences(getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
            }

            sPref.edit().putLong(getString(R.string.pickIdStr), resultBean.getPickID()).commit();
            //Save this for future

            Intent i = new Intent(context, PickUpDetailsActivity.class);
            i.putExtra("pickReqID", resultBean.getPickID());
            i.putExtra("notifiedUsers", resultBean.getNotifiedUsers());
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goHome();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goHome();
                break;
        }
        return true;
    }

    private void goHome(){
        Intent i = new Intent(context, PoolMainActivity.class);
        finish();
        startActivity(i);
    }


}
