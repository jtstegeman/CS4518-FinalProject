package com.jtstegeman.cs4518_finalproject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by zoraver on 2/25/18.
 */

public class TimePickerFragment extends DialogFragment {
    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerFragment(Calendar calendar, TimePickerDialog.OnTimeSetListener listener) {
        super();
        this.calendar = calendar;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }
}
