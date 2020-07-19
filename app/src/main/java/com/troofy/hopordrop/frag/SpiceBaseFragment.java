package com.troofy.hopordrop.frag;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by akshat
 */
public class SpiceBaseFragment extends Fragment {

    protected SpiceManager spiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        spiceManager.start(getActivity());
    }


    @Override
    public void onDetach() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onDetach();
    }


    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }
}
