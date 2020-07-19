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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.ArrayMap;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.adapter.PathListAdapter;
import com.troofy.hopordrop.bean.PathBean;
import com.troofy.hopordrop.request.DeletePathRequest;
import com.troofy.hopordrop.request.PathListRequest;

import java.util.ArrayList;
import java.util.List;

public class PathActivity extends SpiceBaseActivity {

    private ListView pathListView;
    private PathListAdapter pathListAdapter;
    private List pathList = new ArrayList<PathBean>();
    private String token;
    private long authID;
    private Context context;
    private Vibrator vibrator;
    private SharedPreferences defaultSharedPref;
    private TextView headerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        context = this;
        pathListView = (ListView) findViewById(R.id.pathList);
        pathListAdapter = new PathListAdapter(this,pathList);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        defaultSharedPref = getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        authID = defaultSharedPref.getLong("authID", 0);
        token = defaultSharedPref.getString("token", null);

        pathListView.setAdapter(pathListAdapter);
        pathListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                vibrator.vibrate(30);
                final PathBean pathBean = (PathBean) pathListView.getItemAtPosition(i);
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.confirmation))
                        .setMessage("Do you want to delete this PATH ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deletePathFromServer(pathBean.getId());
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return false;
            }
        });

        pathListView.setFooterDividersEnabled(true);
        pathListView.setHeaderDividersEnabled(false);

        //Add header for the list
        View headerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.path_header, null, false);
        headerTxt = (TextView) headerView.findViewById(R.id.pathHeaderTxt);
        pathListView.addHeaderView(headerView);
        loadPathsFromServer();
    }

    private void deletePathFromServer(long pathID) {
        DeletePathRequest request = new DeletePathRequest(context, token, authID, pathID);
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new DeletePathRequestListener());
    }

    private void loadPathsFromServer() {

        PathListRequest request = new PathListRequest(this, token, authID);
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new PathListRequestListener());
    }

    private class PathListRequestListener implements com.octo.android.robospice.request.listener.RequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            startActivity(new Intent(context, ErrorActivity.class));
        }

        @Override
        public void onRequestSuccess(List pathBean) {
            if(pathBean == null || pathBean.size() <= 0){
                headerTxt.setText(R.string.no_paths);
            }else {

                for(Object pathBeanMap: pathBean){
                    PathBean path = new PathBean();
                    path.setId(Long.parseLong(((ArrayMap)pathBeanMap).get("id").toString()));
                    path.setStartAddress(((ArrayMap)pathBeanMap).get("startAddress").toString());
                    path.setDestAddress(((ArrayMap)pathBeanMap).get("destAddress").toString());
                    path.setTime(Long.valueOf(((ArrayMap)pathBeanMap).get("time").toString()));

                    path.setMon(Boolean.valueOf(((ArrayMap)pathBeanMap).get("mon").toString()));
                    path.setTue(Boolean.valueOf(((ArrayMap)pathBeanMap).get("tue").toString()));
                    path.setWed(Boolean.valueOf(((ArrayMap)pathBeanMap).get("wed").toString()));
                    path.setThu(Boolean.valueOf(((ArrayMap)pathBeanMap).get("thu").toString()));
                    path.setFri(Boolean.valueOf(((ArrayMap)pathBeanMap).get("fri").toString()));
                    path.setSat(Boolean.valueOf(((ArrayMap)pathBeanMap).get("sat").toString()));
                    path.setSun(Boolean.valueOf(((ArrayMap)pathBeanMap).get("sun").toString()));

                    pathList.add(path);
                }

            }

            pathListAdapter.notifyDataSetChanged();
        }
    }

    private class DeletePathRequestListener implements com.octo.android.robospice.request.listener.RequestListener<com.google.api.client.http.HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context,context.getString(R.string.err_generic_msg),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(HttpResponse httpResponse) {
            pathList.clear();
            loadPathsFromServer();
            Toast.makeText(context, R.string.path_deleted,Toast.LENGTH_SHORT).show();
        }
    }
}
