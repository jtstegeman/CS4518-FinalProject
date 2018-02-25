package com.jtstegeman.cs4518_finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by zoraver on 2/25/18.
 */

public class DatePickerFragment extends DialogFragment {
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment(Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        super();
        this.calendar = calendar;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
