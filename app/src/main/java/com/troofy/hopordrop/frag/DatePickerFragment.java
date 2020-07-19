package com.troofy.hopordrop.frag;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by akshat
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog datePickerDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        long maxDate = bundle.getLong("maxDate", 0);
        int year = bundle.getInt("year", 2000);
        int month = bundle.getInt("month", 1);
        int day = bundle.getInt("day", 1);

        // Create a new instance of DatePickerDialog and return it
        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Date date = cal.getTime();
        this.dateSelectedListener.onComplete(date);
    }


    public static interface OnDateSelectedListener {
        public abstract void onComplete(Date date);
    }

    private OnDateSelectedListener dateSelectedListener;

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.dateSelectedListener = (OnDateSelectedListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDateSelectedListener");
        }
    }


}
