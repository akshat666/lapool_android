package com.troofy.hopordrop.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.onesignal.OneSignal;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.adapter.CustomDrawerAdapter;
import com.troofy.hopordrop.bean.DrawerItem;
import com.troofy.hopordrop.frag.AboutFragment;
import com.troofy.hopordrop.frag.NetworkDialogFragment;
import com.troofy.hopordrop.frag.NetworkFragment;
import com.troofy.hopordrop.frag.PoolHomeFragment;
import com.troofy.hopordrop.frag.SettingsFragment;
import com.troofy.hopordrop.frag.ShareFragment;
import com.troofy.hopordrop.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class PoolMainActivity extends LocationActivity implements AboutFragment.OnFragmentInteractionListener,
        PoolHomeFragment.OnFragmentInteractionListener, NetworkDialogFragment.NetworkDialogInterface {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout leftDrawer;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CustomDrawerAdapter adapter;
    private SharedPreferences sPref;
    private AccessTokenTracker accessTokenTracker;

    List<DrawerItem> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        //Check if user has logged in correctly
        if(!SystemUtils.isUserDetailedSaved(this)){
            Intent i = new Intent(getApplicationContext(),ErrorActivity.class);
            startActivity(i);
            finish();
        }

        SystemUtils.startLocationAlarm(this);

        sPref =  getSharedPreferences(getString(R.string.defaultAppPref), Context.MODE_PRIVATE);

        //Set notifications ON and counter to 0 when app is opened
        OneSignal.enableSound(true);
        OneSignal.enableVibrate(true);
        sPref.edit().putInt(getString(R.string.noty_count), 0).apply();

        //check if location is enabled
        SystemUtils.checkLocationService(this);

        //Call for setting location data to app variable
        buildGoogleApiClient();

        if(SystemUtils.isPickupReqRunning(this)){
            Intent i = new Intent(this, PickUpDetailsActivity.class);
            startActivity(i);
        }

        //If trip is running go to trip activity
        if(SystemUtils.isTripRunning(this)){
            Intent i = new Intent(this, TripActivity.class);
            startActivity(i);
        }else{
            //Stop the trip alarm service for fail safe
            SystemUtils.stopTripLocationService(this);
        }

        if (!SystemUtils.isNetworkAvailable(this)) {
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);
        }
        setContentView(R.layout.activity_pool_main);

        // Initializing
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_item);
        leftDrawer = (LinearLayout) findViewById(R.id.leftDrawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        //Set FB profile pic in drawer
        SharedPreferences defaultSharedPref = this.getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        String fbUserId = defaultSharedPref.getString("userID", null);
        String name = defaultSharedPref.getString("name", null);
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePic);
        TextView nameView = (TextView) findViewById(R.id.userName);
        nameView.setText(name);
        profilePictureView.setProfileId(fbUserId);

        // Add Drawer Item to dataList
        dataList.add(new DrawerItem(getString(R.string.home), R.drawable.ic_account_balance_black_24dp));
        //dataList.add(new DrawerItem(getString(R.string.network), R.drawable.ic_people_black_24dp));
        dataList.add(new DrawerItem(getString(R.string.share), R.drawable.ic_share_black_24dp));
        dataList.add(new DrawerItem(getString(R.string.settings), R.drawable.ic_settings_black_24dp));
        dataList.add(new DrawerItem(getString(R.string.about), R.drawable.ic_info_black_24dp));

        adapter = new CustomDrawerAdapter(this, R.layout.drawer_list_item,
                dataList);

        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Home up button for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        Toolbar toolbar = new Toolbar(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            SelectItem(0);
        }
    }


    public void SelectItem(int position) {

        android.support.v4.app.Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new PoolHomeFragment();
                break;
//            case 1:
//                fragment = new NetworkFragment();
//
//                break;
            case 1:
                fragment = new ShareFragment();

                break;
            case 2:
                fragment = new SettingsFragment();
                break;
            case 3:
                fragment = new AboutFragment();
                break;

            default:
                break;
        }

        fragment.setArguments(args);
        android.support.v4.app.FragmentManager frgManager = getSupportFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(dataList.get(position).getItemName());
        mDrawerLayout.closeDrawer(leftDrawer);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void tryAgain() {
        startActivity(new Intent(this,PoolMainActivity.class));
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SelectItem(position);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SystemUtils.isPickupReqRunning(this)){
            Intent i = new Intent(this, PickUpDetailsActivity.class);
            startActivity(i);
        }

        //If trip is running go to trip activity
        if(SystemUtils.isTripRunning(this)){
            Intent i = new Intent(this, TripActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {

        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            this.mDrawerLayout.openDrawer(GravityCompat.START);        }

    }
}