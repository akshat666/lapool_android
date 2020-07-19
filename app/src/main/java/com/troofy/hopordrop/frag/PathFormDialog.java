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

package com.troofy.hopordrop.frag;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.GiveDropActivity;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.PathBean;
import com.troofy.hopordrop.bean.PlaceAutocomplete;
import com.troofy.hopordrop.request.GoogleLocationSearchRequest;
import com.troofy.hopordrop.request.SavePathRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by akshat666
 */

public class PathFormDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private DialogFragment dialogFragment;
    private Button timeBtn;
    private android.widget.SearchView startSrchField;
    private android.widget.SearchView destSrchField;
    private android.widget.CursorAdapter suggestionAdapter;
    private android.widget.CursorAdapter suggestionAdapter2;
    protected SpiceManager spiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);
    private ApplicationVariables appVariables;
    private AutocompletePredictionBuffer autocompletePredictions;
    private ArrayList<PlaceAutocomplete> suggestionList;
    private ArrayList<PlaceAutocomplete> suggestionList2;
    private GiveDropActivity activity;
    private String startPlaceID;
    private String startAddress;
    private String destPlaceID;
    private String destAddress;
    private boolean isStartAddress = true;
    private ToggleButton monBtn;
    private ToggleButton tueBtn;
    private ToggleButton wedBtn;
    private ToggleButton thuBtn;
    private ToggleButton friBtn;
    private ToggleButton satBtn;
    private ToggleButton sunBtn;
    private long time;
    private ProgressDialog progressDialog;
    private double startLat;
    private double startLng;
    private double destLat;
    private double destLng;
    private EditText startSrchTxt;
    private EditText destSrchTxt;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialogFragment = this;
        activity = (GiveDropActivity) getActivity();
        if(!spiceManager.isStarted())
        {
            spiceManager.start(activity);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.path_form, null);
        appVariables = (ApplicationVariables) getActivity().getApplication();

        monBtn = (ToggleButton) view.findViewById(R.id.monBtn);
        tueBtn = (ToggleButton) view.findViewById(R.id.tueBtn);
        wedBtn = (ToggleButton) view.findViewById(R.id.wedBtn);
        thuBtn = (ToggleButton) view.findViewById(R.id.thuBtn);
        friBtn = (ToggleButton) view.findViewById(R.id.friBtn);
        satBtn = (ToggleButton) view.findViewById(R.id.satBtn);
        sunBtn = (ToggleButton) view.findViewById(R.id.sunBtn);


        TextView timeTitle = (TextView) view.findViewById(R.id.timeTitleTxt);
        //timeTitle.append(TimeZone.getDefault().getDisplayName());

        builder.setView(view).setTitle(R.string.add_path)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        decodeStartPlaceID();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        timeBtn = (Button) view.findViewById(R.id.pathTimeBtn);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tp1 = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) dialogFragment, 0, 0, false);
                tp1.show();
            }
        });

        suggestionAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        startSrchField = (android.widget.SearchView) view.findViewById(R.id.startSrchFld);
        startSrchField.setIconifiedByDefault(false);
        startSrchField.setSuggestionsAdapter(suggestionAdapter);
        startSrchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startSrchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    isStartAddress = true;
                    performGeoLocationSearch(newText);
                }
                return false;
            }
        });
        startSrchField.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                autocompletePredictions.release();
                startSrchField.setQuery(suggestionList.get(position).description, false);
                startSrchField.clearFocus();
                startSrchTxt.setSelection(0);
                startPlaceID = suggestionList.get(position).placeId.toString();
                startAddress = suggestionList.get(position).description.toString();
                return true;
            }
        });

        int id = startSrchField.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        startSrchTxt = (EditText) startSrchField.findViewById(id);


        suggestionAdapter2 = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        destSrchField = (android.widget.SearchView) view.findViewById(R.id.destSrchFld);
        destSrchField.setIconifiedByDefault(false);
        destSrchField.setSuggestionsAdapter(suggestionAdapter2);

        destSrchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    isStartAddress = false;
                    performGeoLocationSearch(newText);
                }
                return false;
            }
        });
        destSrchField.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                autocompletePredictions.release();
                destSrchField.setQuery(suggestionList2.get(position).description, false);
                destSrchField.clearFocus();
                destSrchTxt.setSelection(0);
                destPlaceID = suggestionList2.get(position).placeId.toString();
                destAddress = suggestionList2.get(position).description.toString();
                return true;
            }
        });
        int id2 = destSrchField.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        destSrchTxt = (EditText) destSrchField.findViewById(id2);

        return builder.create();
    }


    private void decodeStartPlaceID(){

        if (StringUtils.isBlank(startAddress) || StringUtils.isBlank(startPlaceID) ||
                StringUtils.isBlank(destAddress) || StringUtils.isBlank(destPlaceID)) {
            Toast.makeText(getActivity(), R.string.enter_address,Toast.LENGTH_SHORT).show();
            return;
        }
        if(!(monBtn.isChecked() || tueBtn.isChecked() ||
                wedBtn.isChecked() || thuBtn.isChecked() ||
                friBtn.isChecked() || satBtn.isChecked() || sunBtn.isChecked())
                || time <= 0){
            Toast.makeText(getActivity(), R.string.select_day_time,Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog = ProgressDialog.show(getActivity(),getString(R.string.app_name),getString(R.string.wait),false,true);

        //Get more info from placeID
        Places.GeoDataApi.getPlaceById(activity.getmGoogleApiClient(), startPlaceID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place startPlace = places.get(0);
                            startLat = startPlace.getLatLng().latitude;
                            startLng = startPlace.getLatLng().longitude;
                            places.release();

                            decodeDestPlaceID();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),getString(R.string.err_generic_msg),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void decodeDestPlaceID() {
        Places.GeoDataApi.getPlaceById(activity.getmGoogleApiClient(), destPlaceID)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place destPlace = places.get(0);
                            destLat = destPlace.getLatLng().latitude;
                            destLng = destPlace.getLatLng().longitude;
                            places.release();

                            savePath();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),getString(R.string.err_generic_msg),Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void savePath() {

        SharedPreferences sPref = activity.getSharedPreferences(activity.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long authID = sPref.getLong("authID", 0);
        String token = sPref.getString("token", null);

        PathBean path = new PathBean();
        path.setStartAddress(startAddress);
        path.setStartLat(startLat);
        path.setStartLng(startLng);
        path.setDestAddress(destAddress);
        path.setDestLat(destLat);
        path.setDestLng(destLng);
        path.setMon(monBtn.isChecked());
        path.setTue(tueBtn.isChecked());
        path.setWed(wedBtn.isChecked());
        path.setThu(thuBtn.isChecked());
        path.setFri(friBtn.isChecked());
        path.setSat(satBtn.isChecked());
        path.setSun(sunBtn.isChecked());
        path.setTime(time);
        path.setTimeZoneID(TimeZone.getDefault().getID());

        Genson gen = new Genson();
        String json = gen.serialize(path);

        SavePathRequest request = new SavePathRequest(token,authID,json,activity);
        if(!spiceManager.isStarted()){
            spiceManager.start(activity);
        }
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new PathRequestListener());


    }

    private void performGeoLocationSearch(String queryTxt) {

        LatLng latLng = new LatLng(appVariables.getCurrentLocation().getLatitude(), appVariables.getCurrentLocation().getLongitude());
        final LatLngBounds bound = SystemUtils.buildLatLngBound(latLng, 500);
        PendingResult result =
                Places.GeoDataApi.getAutocompletePredictions(activity.getmGoogleApiClient(), queryTxt, bound, null);

        GoogleLocationSearchRequest request = new GoogleLocationSearchRequest(result);
        this.spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, AppConstants.CACHE_GEOSEARCH_KEY, DurationInMillis.ONE_WEEK, new GoogleLocationSearchListener());

    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hr, int min) {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        time = cal.getTimeInMillis();

        timeBtn.setText(cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        spiceManager.start(getActivity());
    }

    @Override
    public void onDetach() {
        if(autocompletePredictions != null){
            autocompletePredictions.release();
        }
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onDetach();
    }


    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    private class GoogleLocationSearchListener implements RequestListener<AutocompletePredictionBuffer> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
        }

        @Override
        public void onRequestSuccess(AutocompletePredictionBuffer buffer) {

            autocompletePredictions = buffer;
            Iterator<AutocompletePrediction> iterator = buffer.iterator();
            if (isStartAddress) {
                suggestionList = new ArrayList<>(buffer.getCount());
            } else {
                suggestionList2 = new ArrayList<>(buffer.getCount());
            }
            String[] columns = {
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
            };
            MatrixCursor cursor = new MatrixCursor(columns);
            int i = 0;
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                if (isStartAddress) {
                    suggestionList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null)));
                } else {
                    suggestionList2.add(new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null)));
                }
                String[] tmp = {Integer.toString(i), prediction.getFullText(null).toString()};
                cursor.addRow(tmp);
                i++;
            }
            if (isStartAddress) {
                suggestionAdapter.swapCursor(cursor);
            } else {
                suggestionAdapter2.swapCursor(cursor);
            }

        }
    }

    private class PathRequestListener implements  RequestListener<HttpResponse>{
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            Toast.makeText(activity, "Error occurred! ",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(HttpResponse httpResponse) {
            progressDialog.dismiss();
            Toast.makeText(activity,"Path saved!",Toast.LENGTH_SHORT).show();
        }
    }
}
