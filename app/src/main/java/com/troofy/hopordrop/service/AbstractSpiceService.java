package com.troofy.hopordrop.service;

import android.app.Service;

import com.octo.android.robospice.SpiceManager;

/**
 * Created by akshat666
 */
public abstract class AbstractSpiceService extends Service {

    protected SpiceManager spiceManager = new SpiceManager(com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService.class);

    @Override
    public void onCreate() {
        super.onCreate();
        spiceManager.start(this);
    }

    @Override
    public void onDestroy() {
        spiceManager.shouldStop();
        super.onDestroy();
    }
}