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

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.troofy.hopordrop.R;
import com.troofy.hopordrop.bean.PathBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by akshat666
 */

public class PathListAdapter extends BaseAdapter {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");


    public PathListAdapter(Context context, List pathList){
        this.context = context;
        this.pathList = pathList;
    }

    private List<PathBean> pathList;
    private LayoutInflater inflater;
    private Context context;


    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public Object getItem(int i) {
        return pathList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View v;
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            v = inflater.inflate(R.layout.path_list_item, null);
            v.setTag(viewHolder);
        } else {
            v = (View) convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.startAdd = (TextView) v.findViewById(R.id.startAddItem);
        viewHolder.destAdd = (TextView) v.findViewById(R.id.destAddItem);
        viewHolder.time = (TextView) v.findViewById(R.id.pathTimeItem);
        viewHolder.sun = (TextView) v.findViewById(R.id.sunFlag);
        viewHolder.mon = (TextView) v.findViewById(R.id.monFlag);
        viewHolder.tue = (TextView) v.findViewById(R.id.tueFlag);
        viewHolder.wed = (TextView) v.findViewById(R.id.wedFlag);
        viewHolder.thu = (TextView) v.findViewById(R.id.thuFlag);
        viewHolder.fri = (TextView) v.findViewById(R.id.friFlag);
        viewHolder.sat = (TextView) v.findViewById(R.id.satFlag);

        PathBean pathBean = pathList.get(position);
        viewHolder.startAdd.setText(pathBean.getStartAddress());
        viewHolder.destAdd.setText(pathBean.getDestAddress());
        viewHolder.time.setText(sdf.format(new Date(pathBean.getTime())));
        if(pathBean.isMon()){
            viewHolder.mon.setTypeface(null, Typeface.BOLD);
            viewHolder.mon.setPaintFlags(viewHolder.mon.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isTue()){
            viewHolder.tue.setTypeface(null, Typeface.BOLD);
            viewHolder.tue.setPaintFlags(viewHolder.tue.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isWed()){
            viewHolder.wed.setTypeface(null, Typeface.BOLD);
            viewHolder.wed.setPaintFlags(viewHolder.wed.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isThu()){
            viewHolder.thu.setTypeface(null, Typeface.BOLD);
            viewHolder.thu.setPaintFlags(viewHolder.thu.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isFri()){
            viewHolder.fri.setTypeface(null, Typeface.BOLD);
            viewHolder.fri.setPaintFlags(viewHolder.fri.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isSat()){
            viewHolder.sat.setTypeface(null, Typeface.BOLD);
            viewHolder.sat.setPaintFlags(viewHolder.sat.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        if(pathBean.isSun()){
            viewHolder.sun.setTypeface(null, Typeface.BOLD);
            viewHolder.sun.setPaintFlags(viewHolder.sun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        return  v;
    }

    private static class ViewHolder {
        TextView startAdd;
        TextView destAdd;
        TextView time;
        TextView sun;
        TextView mon;
        TextView tue;
        TextView wed;
        TextView thu;
        TextView fri;
        TextView sat;

    }
}
