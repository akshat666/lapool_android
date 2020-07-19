package com.troofy.hopordrop.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.adapter.DropPageAdapter;
import com.troofy.hopordrop.frag.NetworkDialogFragment;
import com.troofy.hopordrop.util.SystemUtils;

public class GiveDropActivity extends LocationActivity implements TabLayout.OnTabSelectedListener{

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;
    int selectedTab;
    private DropPageAdapter adapter;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_give_drop);

        context = this;

        if(!SystemUtils.isNetworkAvailable(this)){
            // No internet
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            NetworkDialogFragment dialogFrag = new NetworkDialogFragment();
            dialogFrag.setCancelable(false);
            dialogFrag.getDialog().setCanceledOnTouchOutside(false);
            dialogFrag.show(transaction, NetworkDialogFragment.TAG);
        }

        if(SystemUtils.isTripRunning(this)){
            Intent i = new Intent(this,TripActivity.class);
            startActivity(i);
            finish();
        }

        if(!SystemUtils.isUserDetailedSaved(this)){
            Intent i = new Intent(getApplicationContext(),ErrorActivity.class);
            startActivity(i);
            finish();
        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_public_black_24dp).setText(getString(R.string.publicStr)));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_people_black_24dp).setText(getString(R.string.networkStr)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(ContextCompat.getColor(this,(R.color.hint)),ContextCompat.getColor(this,(R.color.black)));

        //Check which tab should be selected when loading this activity
        Intent i = getIntent();
        selectedTab = i.getIntExtra("selectedTab",0);
        tabLayout.getTabAt(selectedTab).select();

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new DropPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());


        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        final ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(viewPagerListener);

        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);

        //Solving the bug where the tabs when selected from another
        // activity caused the viewPager not to call its listeners
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(selectedTab);
                viewPagerListener.onPageSelected(viewPager.getCurrentItem());
            }
        });



    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goHome();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goHome();
                break;
        }
        return true;
    }

    private void goHome(){
        Intent i = new Intent(context, PoolMainActivity.class);
        finish();
        startActivity(i);
    }

}
