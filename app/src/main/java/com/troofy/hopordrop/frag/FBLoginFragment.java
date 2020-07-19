package com.troofy.hopordrop.frag;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.octo.android.robospice.Jackson2GoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.onesignal.OneSignal;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.LoginActivity;
import com.troofy.hopordrop.activity.PoolMainActivity;
import com.troofy.hopordrop.activity.UserDetailActivity;
import com.troofy.hopordrop.bean.FBLoginBean;
import com.troofy.hopordrop.bean.UserBean;
import com.troofy.hopordrop.receiver.AlarmReceiver;
import com.troofy.hopordrop.request.CheckUserInSystemRequest;
import com.troofy.hopordrop.util.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class FBLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    protected SpiceManager spiceManager = new SpiceManager(Jackson2GoogleHttpClientSpiceService.class);

    private static final String TAG = "FBLoginFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LoginButton loginButton;

    private CallbackManager callbackManager;

    private String email = null;
    private String name = null;
    private String gender = null;
    private String bday = null;
    private LoginResult fbLoginResult;
    private Context context;
    private String userID;
    private ProgressDialog progressDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FBLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FBLoginFragment newInstance(String param1, String param2) {
        FBLoginFragment fragment = new FBLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FBLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //TODO : Implement ProfileTracker and AccessTokenTracker
        // This is needed to know if user changes anything in his profile
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getActivity());

        // Inflate the drawer_list_item for this fragment
        View view = inflater.inflate(R.layout.fragment_fblogin, container, false);

        //teerms and policy hyperlink
        TextView terms =(TextView)view.findViewById(R.id.termTxt);
        terms.setClickable(true);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        String termsTxt = getString(R.string.termsLink);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            terms.setText(Html.fromHtml(termsTxt,Html.FROM_HTML_MODE_LEGACY));
        } else {
            terms.setText(Html.fromHtml(termsTxt));
        }

        TextView policy =(TextView)view.findViewById(R.id.policyTxt);
        policy.setClickable(true);
        policy.setMovementMethod(LinkMovementMethod.getInstance());
        String policyTxt = getString(R.string.policyLink);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            policy.setText(Html.fromHtml(policyTxt,Html.FROM_HTML_MODE_LEGACY));
        } else {
            policy.setText(Html.fromHtml(policyTxt));
        }

        this.context = getActivity();

        ImageView info = (ImageView)view.findViewById(R.id.logInfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Social login");
                builder1.setIcon(R.drawable.ic_info_outline_black_24dp).setIconAttribute(android.R.attr.alertDialogIcon);
                builder1.setMessage(R.string.login_btn_info);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        null,
                        null);

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));

        // If using in a fragment
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                fbLoginResult = loginResult;
                                userID = loginResult.getAccessToken().getUserId();
                                try {
                                    email = object.getString("email");
                                    name = object.getString("name");
                                    gender = object.getString("gender");
                                    //bday = object.getString("birthday");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Check if user is already in the System
                                // Send fb token details as well so that tokens are up to date
                                FBLoginBean fbLoginBean = new FBLoginBean();
                                fbLoginBean.setToken(loginResult.getAccessToken().getToken());
                                fbLoginBean.setExpires(loginResult.getAccessToken().getExpires().getTime());
                                fbLoginBean.setLastRefresh(loginResult.getAccessToken().getLastRefresh().getTime());
                                fbLoginBean.setSource(loginResult.getAccessToken().getSource().name());
                                fbLoginBean.setApplicationID(loginResult.getAccessToken().getApplicationId());

                                UserBean userBean = new UserBean();
                                userBean.setUserID(loginResult.getAccessToken().getUserId());
                                userBean.setProviderType(AppConstants.PROVIDER_TYPE_FACEBOOK);
                                userBean.setFbLoginBean(fbLoginBean);
                                checkUserExists(userBean);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

                //Check if all permissions are granted
//                if(loginResult.getRecentlyDeniedPermissions().size() > 0)
//                {
//                    List<String> deniedPerms = new ArrayList<>(loginResult.getRecentlyDeniedPermissions());
//                    LoginManager.getInstance().logInWithReadPermissions(getActivity(),deniedPerms);
//                }
//                else {
//                    //Save data to cloud DB
//                    appVariables = (ApplicationVariables)getActivity().getApplicationContext();
//                    appVariables.setUserID(loginResult.getAccessToken().getUserId());
//                    token = loginResult.getAccessToken().getToken();
//                    //performRequest(loginResult);
//                    //startActivity(intent);
//                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Please re-login and accept the permissions",
                        Toast.LENGTH_LONG).show();
                getActivity().finish();
                //System.exit(0);
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), "Error occured",
                        Toast.LENGTH_LONG).show();
                getActivity().finish();
                //System.exit(0);
                // App code
            }
        });

        return view;
    }

    private void checkUserExists(UserBean userBean) {
        progressDialog = ProgressDialog.show(context,getString(R.string.app_name),getString(R.string.wait),false,false);


        Genson genson = new Genson();
        String json = genson.serialize(userBean);
        CheckUserInSystemRequest request = new CheckUserInSystemRequest(json,getActivity());
        this.spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new CheckUserInSystemListener());

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        spiceManager.start(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }


    private class CheckUserInSystemListener implements com.octo.android.robospice.request.listener.RequestListener<JSONObject> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(),
                    R.string.err_generic_msg, Toast.LENGTH_SHORT)
                    .show();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            getActivity().finish();
        }

        @Override
        public void onRequestSuccess(JSONObject userJsonObj) {
            progressDialog.dismiss();
            long authID = 0;
            String userID = null;
            String token = null;
            String fullName = null;
            String gen = null;
            try {
                authID = userJsonObj.getLong("authID");
                userID = userJsonObj.getString("userID");
                token = userJsonObj.getString("token");
                fullName = userJsonObj.getString("name");
                gen = userJsonObj.getString("gender");
                if(gen != null && !gen.isEmpty()){
                    if(gen.equalsIgnoreCase("m") || gen.equalsIgnoreCase("male")){
                        gen = "male";
                    }else{
                        gen = "female";
                    }
                }

            } catch (Exception e) {
            }

            if(userJsonObj != null && authID > 0){

                SharedPreferences defaultSharedPref = getActivity().getSharedPreferences(getActivity().getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = defaultSharedPref.edit();
                editor.putLong(context.getString(R.string.authIDStr), authID);
                editor.putString(context.getString(R.string.token), token);
                editor.putString("userID", userID);
                editor.putString("name", fullName);
                editor.putString("gender", gen);
                editor.apply();

                OneSignal.sendTag("authID", Long.toString(authID));
                OneSignal.setSubscription(true);
                //Register an alarm manager
                //If no alarm is set
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                if(!defaultSharedPref.getBoolean("isLocationAlarmSet",false)){
                    AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime(),
                            AppConstants.alarmInterval,
                            pendingIntent);
                    editor = defaultSharedPref.edit();
                    editor.putBoolean("isLocationAlarmSet",true);
                    editor.commit();
                }

                //Check the current user status and navigate accordingly
                ((LoginActivity)getActivity()).fetchUserState();

//                Intent intent = new Intent(context, PoolMainActivity.class);
//                startActivity(intent);
//                getActivity().finish();

            }
            //If new user registration
            else{

                Intent i = new Intent(getActivity(), UserDetailActivity.class);
                i.putExtra("email", email);
                i.putExtra("name", name);
                i.putExtra("gender", gender);
                i.putExtra("birthday", bday);
                i.putExtra("token", fbLoginResult.getAccessToken().getToken());
                i.putExtra("applicationID", fbLoginResult.getAccessToken().getApplicationId());
                i.putExtra("expires", fbLoginResult.getAccessToken().getExpires().getTime());
                i.putExtra("lastRefresh", fbLoginResult.getAccessToken().getLastRefresh().getTime());
                i.putExtra("userID", fbLoginResult.getAccessToken().getUserId());
                i.putExtra("source", fbLoginResult.getAccessToken().getSource().name());

                startActivity(i);
                getActivity().finish();

            }

        }
    }

}
