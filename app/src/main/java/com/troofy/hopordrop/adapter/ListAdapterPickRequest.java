/*
 * ************************************************************************
 *  *
 *  * TROOFY LABS - CONFIDENTIAL
 *  * __________________
 *  *  Copyright (c) 2016.
 *  *
 *  *  All Rights Reserved.
 *  *
 *  * NOTICE:  All information contained herein is, and remains
 *  * the property of Troofy Labs(OPC) Private Limited and its suppliers,
 *  * if any.  The intellectual and technical concepts contained
 *  * herein are proprietary to Troofy Labs(OPC) Private Limited
 *  * and its suppliers and may be covered by U.S. and Foreign Patents,
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material
 *  * is strictly forbidden unless prior written permission is obtained
 *  * from Troofy Labs(OPC) Private Limited.
 *
 */

package com.troofy.hopordrop.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PickUpRequestBean;
import com.troofy.hopordrop.util.AppConstants;
import com.troofy.hopordrop.util.SystemUtils;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by akshat666s
 */
public class ListAdapterPickRequest extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<PickUpRequestBean> pickUpRequestBeans;
    DecimalFormat df = new DecimalFormat("00.00");

    public ListAdapterPickRequest(Context context, List<PickUpRequestBean> pickUpRequestBeans) {
        this.context = context;
        this.pickUpRequestBeans = pickUpRequestBeans;
    }

    @Override
    public int getCount() {
        return pickUpRequestBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return pickUpRequestBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View v;
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            v = inflater.inflate(R.layout.list_row, null);
            v.setTag(viewHolder);
        } else {
            v = (View) convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.thumbNail = (ProfilePictureView) v
                .findViewById(R.id.thumbnail);
        viewHolder.title = (TextView) v.findViewById(R.id.name);
        viewHolder.gender = (TextView) v.findViewById(R.id.gender);
        viewHolder.age = (TextView) v.findViewById(R.id.age);
        viewHolder.distance = (TextView) v.findViewById(R.id.itemDistance);
        viewHolder.dropLoc = (TextView) v.findViewById(R.id.dropLoc);
        viewHolder.seats = (TextView) v.findViewById(R.id.itemSeats);

        //TextView pickTime = (TextView)convertView.findViewById(R.id.itemPickTime);

        PickUpRequestBean pickUpRequest = pickUpRequestBeans.get(position);

        // thumbnail image
        viewHolder.thumbNail.setProfileId(pickUpRequest.getUserID());

        // title
        viewHolder.title.setText(pickUpRequest.getName().toUpperCase());

        //Sex
        if (pickUpRequest.getGender() == 'm') {
            viewHolder.gender.setText("Male");
        } else {
            viewHolder.gender.setText("Female");
        }

        //Age
        viewHolder.age.setText(SystemUtils.returnAgeGroup(pickUpRequest.getAge()) + " Yrs");

        //Distance from user
        viewHolder.distance.setText(df.format(pickUpRequest.getDistanceFromUser() / 1000) + " KM away");

        //Drop Location
        viewHolder.dropLoc.setText(pickUpRequest.getDropAddress());


        //Seats
        viewHolder.seats.setText(context.getResources().getQuantityString(R.plurals.request,2) + " " + String.valueOf(pickUpRequest.getSeats()) + " " +
                context.getResources().getQuantityString(R.plurals.seats,pickUpRequest.getSeats()));


        //Condition set here - where images only where repeating in the view
        if (convertView == null) {
            //Linked networks
            viewHolder.linkedNwLatout = (LinearLayout) v.findViewById(R.id.linkedNetworksImg);
            for (Integer nw : (List<Integer>) pickUpRequest.getNetworkLinked()) {

                if (nw == AppConstants.NETWORK_FACEBOOK) {
                    TextView fbTxt = new TextView(context);
                    fbTxt.setText(R.string.fb);
                    fbTxt.setTypeface(fbTxt.getTypeface(), Typeface.BOLD);
                    fbTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                    viewHolder.linkedNwLatout.addView(fbTxt);
                } else if (nw == AppConstants.NETWORK_WORK) {
                    ImageView workImg = new ImageView(context);
                    workImg.setImageResource(R.drawable.fb_icon);
                    workImg.setLayoutParams(new ViewGroup.LayoutParams(33, 33));
                    workImg.requestLayout();
                    viewHolder.linkedNwLatout.addView(workImg);
                }
            }

            //Pick time
            //pickTime.setText(new SimpleDateFormat(activity.getString(R.string.dateTimeFormat)).format(pickUpRequest.getCreatedDate()));
        }

        return v;
    }

    private static class ViewHolder {
        ProfilePictureView thumbNail;
        TextView title;
        TextView gender;
        TextView age;
        TextView distance;
        TextView dropLoc;
        TextView seats;
        LinearLayout linkedNwLatout;

    }
}
