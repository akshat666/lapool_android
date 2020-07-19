package com.troofy.hopordrop.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.onesignal.OneSignal;
import com.owlike.genson.Genson;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.FBLoginBean;
import com.troofy.hopordrop.bean.UserBean;
import com.troofy.hopordrop.frag.DatePickerFragment;
import com.troofy.hopordrop.frag.DatePickerFragment.OnDateSelectedListener;
import com.troofy.hopordrop.request.FBUserSaveRequest;
import com.troofy.hopordrop.util.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDetailActivity extends SpiceBaseActivity implements OnDateSelectedListener{

    private FBLoginBean fbLoginBean;
    private String gender;
    private ApplicationVariables appVariables;
    private TextView bdayfld;
    private String userId;
    private String name;
    private Button confirm;
    private boolean isMale = false;
    private boolean isFemale = false;
    private ProgressDialog progressDialog;
    private Context context;
    private TextView countryCode;
    private TextView phoneTxt;
    private static final int SMS_REQUEST_CODE = 69;
    private View view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_acitivity);

        appVariables = (ApplicationVariables)this.getApplicationContext();

        final TextView nameTxt = (TextView) findViewById(R.id.namefld);
        nameTxt.setText(getIntent().getStringExtra("name"));

        final TextView emailTxt = (TextView) findViewById(R.id.emailfld);
        emailTxt.setText(getIntent().getStringExtra("email"));

        final ImageView male = (ImageView)findViewById(R.id.maleBtn);
        final ImageView female = (ImageView)findViewById(R.id.femaleBtn);

        countryCode = (TextView) findViewById(R.id.countryCodeFld);
        countryCode.setText(SystemUtils.getCountryZipCode(this));

        this.context =this;

        if("male".equalsIgnoreCase(getIntent().getStringExtra("gender"))){
            female.setAlpha(0.2f);
            isMale=true;
        }else{
            male.setAlpha(0.2F);
            isFemale=true;
        }

        phoneTxt = (TextView) findViewById(R.id.phonefld);
        phoneTxt.setText(getIntent().getStringExtra("phone"));

        fbLoginBean = new FBLoginBean();
        fbLoginBean.setApplicationID(getIntent().getStringExtra("applicationID"));
        fbLoginBean.setExpires(getIntent().getLongExtra("expires", 0));
        fbLoginBean.setLastRefresh(getIntent().getLongExtra("lastRefresh", 0));
        fbLoginBean.setToken(getIntent().getStringExtra("token"));
        fbLoginBean.setUserID(getIntent().getStringExtra("userID"));
        fbLoginBean.setSource(getIntent().getStringExtra("source"));

        bdayfld = (TextView) findViewById(R.id.bdayfld);
        bdayfld.setKeyListener(null);
        bdayfld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        //To hide keyboard when datePicker is popped
        bdayfld.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = bdayfld.getInputType(); // backup the input type
                bdayfld.setInputType(InputType.TYPE_NULL); // disable soft input
                bdayfld.onTouchEvent(event); // call native handler
                bdayfld.setInputType(inType); // restore input type
                return true; // consume touch even
            }
        });

        confirm = (Button)findViewById(R.id.confirmBtn);
        confirm.setEnabled(true);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SystemUtils.isEmpty(nameTxt) ||
                        SystemUtils.isEmpty(emailTxt) ||
                        SystemUtils.isEmpty(phoneTxt) ||
                        SystemUtils.isEmpty(bdayfld) ||
                        SystemUtils.isEmpty(countryCode) ||
                        (!isMale && !isFemale)){

                    Toast.makeText(getBaseContext(),
                            "Please fill all details! " , Toast.LENGTH_SHORT)
                            .show();
                    return;

                }

                if(!SystemUtils.isValidEmail(emailTxt.getText())){
                    Toast.makeText(getBaseContext(),
                            R.string.enter_valid_email , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }


                if(!SystemUtils.isValidMobile(phoneTxt.getText().toString().trim())){
                    Toast.makeText(getBaseContext(),
                            R.string.enter_valid_mobile , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(!SystemUtils.isOnlyDigits(countryCode.getText().toString().trim())){
                    Toast.makeText(getBaseContext(),
                            R.string.enter_digits_countrycode , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                fbLoginBean.setName(nameTxt.getText().toString());
                fbLoginBean.setEmail(emailTxt.getText().toString());
                fbLoginBean.setPhone(countryCode.getText().toString()+"-"+phoneTxt.getText().toString());

                userId = getIntent().getStringExtra("userID");
                name = nameTxt.getText().toString().trim();
                gender = getIntent().getStringExtra("gender");

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = formatter.parse(bdayfld.getText().toString());
                    fbLoginBean.setBirthday(date.getTime());

                } catch (java.text.ParseException pe) {
                }

                confirm.setEnabled(false);

                if (isMale) {
                    fbLoginBean.setGender('m');
                } else {
                    fbLoginBean.setGender('f');
                }

                view = v;
                //SMS Confirmation activity to be called
                onLoginPhone(view);

            }
        });

    }

    private void sendUserDetailsToServer() {
        progressDialog = ProgressDialog.show(this,getString(R.string.finishReg),getString(R.string.wait),false,false);
        Genson genson = new Genson();

        String fbLoginJsonString = genson.serialize(fbLoginBean);
        FBUserSaveRequest request = new FBUserSaveRequest(fbLoginJsonString,this);
        request.setRetryPolicy(null);

        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new UserDetailActivity.FBUserSaveListener());

    }

    @Override
    public void onComplete(Date date) {

        fbLoginBean.setBirthday(date.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        bdayfld.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
        bdayfld.clearFocus();
    }

    // ============================================================================================
    // INNER CLASSES
    // ============================================================================================
    public final class FBUserSaveListener implements RequestListener<UserBean> {


        @Override
        public void onRequestFailure( SpiceException spiceException ) {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(),
                    R.string.err_generic_msg, Toast.LENGTH_SHORT)
                    .show();
            confirm.setEnabled(true);
            return;
        }

        @Override
        public void onRequestSuccess(UserBean userBean) {

            progressDialog.dismiss();
            SharedPreferences defaultSharedPref = context.getSharedPreferences(context.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = defaultSharedPref.edit();
            editor.putLong("authID", userBean.getAuthID());
            editor.putString("token", userBean.getToken());
            editor.putString("userID", userId);
            editor.putString("name", name);
            editor.putString("gender",gender);
            editor.commit();

            OneSignal.sendTag("authID", Long.toString(userBean.getAuthID()));
            OneSignal.setSubscription(true);
            //Start location alarm
                SystemUtils.startLocationAlarm(context);
                Intent intent = new Intent(context, PoolMainActivity.class);
                startActivity(intent);
                finish();
        }
    }

    public void showDatePicker(View view){
        DatePickerFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getFragmentManager(), "datePicker");
        Bundle bundle = new Bundle();
        bundle.putLong("maxDate", System.currentTimeMillis());
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        bundle.putInt("year",year - 18);
        bundle.putInt("month",month);
        bundle.putInt("day",day);

        dialogFragment.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {

    }

    public void onLoginPhone(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setReceiveSMS(true);
        configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
        configurationBuilder.setInitialPhoneNumber(new PhoneNumber(countryCode.getText().toString().trim(),phoneTxt.getText().toString().trim(),null));
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, SMS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != SMS_REQUEST_CODE) {
            return;
        }

        AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
        if (loginResult.getError() != null) {
            String toastMessage = loginResult.getError().getErrorType().getMessage();
            return;
        } else if (loginResult.wasCancelled()) {
            String toastMessage = "Login Cancelled";
            return;
        }

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                fbLoginBean.setPhone(phoneNumber.toString());

                sendUserDetailsToServer();

            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
            }
        });


    }



}
