package com.troofy.hopordrop.activity;

import android.support.v7.app.AppCompatActivity;

import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;

public abstract class SpiceBaseActivity extends AppCompatActivity {

    protected SpiceManager spiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

//------------------------------------------------------------------------
//---------end of block that can fit in a common base class for all activities
//------------------------------------------------------------------------


}
