package com.troofy.hopordrop.frag;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpResponse;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.ErrorActivity;
import com.troofy.hopordrop.bean.EmailChannelBean;
import com.troofy.hopordrop.bean.UserChannelBean;
import com.troofy.hopordrop.request.SaveEmailRequest;
import com.troofy.hopordrop.request.UserChannelRequest;
import com.troofy.hopordrop.util.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends SpiceBaseFragment implements WorkEmailFragmentDialog.WorkEmailDialogInterface{

    private android.support.v4.app.FragmentManager fragmentManager;
    private String workEmailID;
    private Bundle args;
    private long authID;
    private String token;
    private ProgressDialog progressDialog;
    private TextView email;
    private CheckBox emailCheckBox;
    private ArrayList<UserChannelBean> userChannelList;
    private Context context;

    public NetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        SharedPreferences sPref;
        sPref = getActivity().getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        this.authID = sPref.getLong("authID", 0);
        this.token = sPref.getString("token", null);
        this.context = getActivity();
        this.emailCheckBox = (CheckBox) view.findViewById(R.id.workEmailCheckBox);

        //Fetch work email if present and load it into the field
        fetchUserChannels(this.authID,this.token);

        // Pass the email to the dialog fragment
        args = new Bundle();
        args.putString("workEmailID",this.workEmailID );

        this.fragmentManager = getActivity().getSupportFragmentManager();

        this.email = (TextView) view.findViewById(R.id.workEmailID);
        if(this.workEmailID == null || this.workEmailID.trim().isEmpty()){
            emailCheckBox.setEnabled(false);
        }
        email.setText(this.workEmailID);
        email.setFocusable(false);
        email.setTextSize(13f);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkEmailFragmentDialog dialogFrag = new WorkEmailFragmentDialog();
                dialogFrag.setArguments(args);
                dialogFrag.show(fragmentManager, WorkEmailFragmentDialog.TAG);
            }
        });

        return  view;
    }

    private void fetchUserChannels(long authID, String token) {
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Fetching data...");
        this.progressDialog.setTitle("Loading your Networks");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        UserChannelRequest userChannelRequest = new UserChannelRequest(authID,token,getActivity());
        getSpiceManager().execute(userChannelRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new UserChannelsRequestListener());

    }

    private void saveEmailID(String workEmail,String code, long authID, String token) {

        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage("Processing data...");
        this.progressDialog.setTitle("Email verification");
        this.progressDialog.show();

        // Send email to server for verification
        EmailChannelBean emailChannelBean = new EmailChannelBean();
        emailChannelBean.setAuthId(authID);
        emailChannelBean.setEmailId(workEmail);
        emailChannelBean.setChannelID(code);

        Genson genson = new Genson();
        String emailVerifyJson = genson.serialize(emailChannelBean);
        SaveEmailRequest saveEmailRequest = new SaveEmailRequest(emailVerifyJson,token,authID,getActivity());
        saveEmailRequest.setRetryPolicy(null);
        getSpiceManager().execute(saveEmailRequest, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new EmailVerificationListener());

    }

    @Override
    public void verifyEmail(String email, String code) {
        saveEmailID(email,code,this.authID, this.token);
        this.workEmailID = email;
    }

    @Override
    public Activity getActivityFromParent() {
        return getActivity();
    }

    private class EmailVerificationListener implements com.octo.android.robospice.request.listener.RequestListener<HttpResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(),
                    "Your work channel might not be registered. Please contact support for more help.", Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onRequestSuccess(HttpResponse response) {
            progressDialog.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle("Email Verification")
                    .setMessage("A message with instructions has been sent to the provided email address")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();

            email.setText(workEmailID);
            emailCheckBox.setChecked(false);
        }
    }

    private class UserChannelsRequestListener implements com.octo.android.robospice.request.listener.RequestListener<List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            Intent i = new Intent(context, ErrorActivity.class);
            startActivity(i);

        }

        @Override
        public void onRequestSuccess(List list) {

            userChannelList = new ArrayList<UserChannelBean>();
            if(list == null || list.isEmpty()){
                email.setText("Tap to add Work Email");
                progressDialog.dismiss();
                return;
            }
            for(JSONObject obj : (List<JSONObject>) list){
                UserChannelBean userChannelBean = new UserChannelBean();
                try{
                    userChannelBean.setAuthID(obj.getLong("authID"));
                    userChannelBean.setChannel(obj.getString("channel"));
                    userChannelBean.setEmail(obj.getString("email"));
                    userChannelBean.setStatus(obj.getInt("status"));
                    userChannelList.add(userChannelBean);
                }catch (JSONException je){
                }

            }

            email.setText(userChannelList.get(0).getEmail());
            if (userChannelList.get(0).getStatus() == AppConstants.STATUS_USERCHANNEL_ACTIVE){
                emailCheckBox.setChecked(true);
            }else{
                emailCheckBox.setChecked(false);
            }
            progressDialog.dismiss();

        }
    }
}
