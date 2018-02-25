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
    private TimePickerDialog.OnTimeSetListener listener;

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        return new TimePickerDialog(getActivity(), listener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
    }
}
