package com.troofy.hopordrop.frag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.activity.ErrorActivity;
import com.troofy.hopordrop.activity.PickUpConfirmationActivity;
import com.troofy.hopordrop.activity.PoolMainActivity;
import com.troofy.hopordrop.adapter.ListAdapterPickRequest;
import com.troofy.hopordrop.application.ApplicationVariables;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.request.RidesListRequest;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshat666
 */
public class NetworkHopRequestsFragment extends LocationBaseFragment {

    private int pageNumber = 0;
    private int pageSize = 10;
    private List<PickUpRequestBean> pickUpRequestList = new ArrayList<PickUpRequestBean>();
    private ListAdapterPickRequest listAdapterPickRequest;
    ApplicationVariables appVariables;
    private boolean loading = false;
    private ListView listView;
    private View footerView;
    private TextView footerTxt;
    private Context context;
    private boolean userScrolled = false;
    private boolean isDataFetching = false;
    private Vibrator vibrator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.network_hop_list, container, false);
        context = getActivity();
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        appVariables = (ApplicationVariables)getActivity().getApplication();

        listAdapterPickRequest = new ListAdapterPickRequest(getContext(), pickUpRequestList);
        this.footerView = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        footerView.setClickable(false);
        footerView.setEnabled(false);
        this.footerTxt = (TextView)footerView.findViewById(R.id.footerTxt);

        this.listView = (ListView) view.findViewById(R.id.myNwRequestlist);
        listView.addFooterView(footerView);

        this.listView.setAdapter(listAdapterPickRequest);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //return if clicked on footer
                if(view == footerView){
                    return;
                }
                PickUpRequestBean pickRequest = (PickUpRequestBean)listView.getItemAtPosition(position);

                Intent i = new Intent(getContext(),PickUpConfirmationActivity.class);
                i.putExtra("pickUpRequest",pickRequest);
                startActivity(i);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    userScrolled = true;
                }else{
                    userScrolled = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0 && userScrolled)
                {
                    if(loading == false)
                    {
                        loading = true;
                        loadPickRequestsFromServer();
                    }
                }

            }
        });

        //Set the floating button
        FloatingActionButton addPathBtn = (FloatingActionButton) view.findViewById(R.id.nwFloatingBtn);
        addPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(30);
                PathFormDialog dialog = new PathFormDialog();
                dialog.show(getActivity().getFragmentManager(),"publicAddPath");
            }
        });

        return  view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            loadPickRequestsFromServer();
        }
    }

    private void loadPickRequestsFromServer() {
        isDataFetching = true;

        SharedPreferences defaultSharedPref = getActivity().getSharedPreferences(this.getString(R.string.defaultAppPref), Context.MODE_PRIVATE);
        long authId = defaultSharedPref.getLong("authID", 0);
        String token = defaultSharedPref.getString("token", null);

        String lat = null;
        String lng = null;

        if(mLastLocation != null && mLastLocation.getLatitude() <= 0){
            lat = Double.toString(appVariables.getCurrentLocation().getLatitude());
            lng = Double.toString(appVariables.getCurrentLocation().getLongitude());
        }else if( null != appVariables.getCurrentLocation()){
            lat = Double.toString(appVariables.getCurrentLocation().getLatitude());
            lng = Double.toString(appVariables.getCurrentLocation().getLongitude());
        }
        else{
            Toast.makeText(context, R.string.location_unavailable_settings, Toast.LENGTH_LONG).show();
            Intent i = new Intent(context,PoolMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(i);
        }

        pageNumber++;

        //String cacheKey = "nwPickReqListKey";

        RidesListRequest request = new RidesListRequest(getContext(), authId,token, lat,lng,pageNumber, pageSize, false);
        //spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, AppConstants.CACHE_PICKREQ_DURATION, new RidesListResultListener());
        spiceManager.execute(request, System.currentTimeMillis(), DurationInMillis.ALWAYS_EXPIRED, new RidesListResultListener());

    }

    private class RidesListResultListener implements RequestListener<PickUpRequestBean.PickReqList> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            isDataFetching = false;
            Intent i = new Intent(context,ErrorActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(i);

        }

        @Override
        public void onRequestSuccess(PickUpRequestBean.PickReqList pickupList) {
            loading = false;
            isDataFetching = false;
            if(null == pickupList || pickupList.isEmpty()){
                footerTxt.setText(R.string.no_hop_reqs);
                return;
            }

            for(PickUpRequestBean pick : pickupList){
                    pickUpRequestList.add(pick);
            }
            footerTxt.setText("");

            listAdapterPickRequest.notifyDataSetChanged();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SystemUtils.isNetworkAvailable(getContext())) {
            // No internet
            Intent i = new Intent(context,ErrorActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(i);

        }

        if (!isDataFetching) {
            loadPickRequestsFromServer();
        }
    }
}
