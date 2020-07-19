package com.troofy.hopordrop.frag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.onesignal.OneSignal;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.EmergencyContactActivity;
import com.troofy.hopordrop.activity.LoginActivity;
import com.troofy.hopordrop.activity.PathActivity;
import com.troofy.hopordrop.util.SystemUtils;


public class SettingsFragment extends Fragment {

    private LoginButton loginButton;
    private LinearLayout emerContactLayout;
    private LinearLayout pathLayout;


    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getContext());

        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        emerContactLayout = (LinearLayout) view.findViewById(R.id.emerContLayout);
        emerContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EmergencyContactActivity.class));
            }
        });
        pathLayout = (LinearLayout) view.findViewById(R.id.pathLayout);
        pathLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PathActivity.class));
            }
        });
        loginButton = (LoginButton) view.findViewById(R.id.fbBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SystemUtils.clearSharedPref(getContext());
                OneSignal.setSubscription(false);
                LoginManager.getInstance().logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });







        return view;
    }

}
