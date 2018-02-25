package com.jtstegeman.cs4518_finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;

import java.util.Calendar;

public class MakeAlarm extends AppCompatActivity {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_ALARM_NAME = "alarm_name";
    public static final int MODE_MAKE = 1;
    public static final int MODE_EDIT = 2;

    private Calendar calendar;

    private EditText eventName, locationName;

    public static Intent getMakeAlarmIntent(Context context) {
        Intent intent = new Intent(context, MakeAlarm.class);
        intent.putExtra(EXTRA_MODE, MODE_MAKE);
        return intent;
    }

    public static Intent getEditAlarmIntent(Context context, AlarmObject alarm) {
        Intent intent = new Intent(context, MakeAlarm.class);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        intent.putExtra(EXTRA_ALARM_NAME, alarm.getName());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();

        eventName = findViewById(R.id.newName);
        locationName = findViewById(R.id.newLocation);
    }

    public void selectNewLocation(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void selectNewDate(View v) {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Toast.makeText(MakeAlarm.this, String.format("%d/%d/%d", month, day, year), Toast.LENGTH_LONG).show();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
            }
        });
        picker.show(getFragmentManager(), "datePicker");
    }

    public void selectNewTime(View v) {
        TimePickerFragment picker = new TimePickerFragment();
        picker.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                Toast.makeText(MakeAlarm.this, String.format("%d:%d", hourOfDay, minute), Toast.LENGTH_LONG).show();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });
        picker.show(getFragmentManager(), "timePicker");
    }

    public void cancelNewAlarm(View v) {
        finish();
    }
    public void confirmNewAlarm(View v) {
        AlarmObject alarm = new AlarmObject(eventName.getText().toString());
        alarm.setLocation(locationName.getText().toString());
        alarm.setTime(calendar.getTime());
        AlarmHelper.getInstance(this).create(alarm);
        finish();
    }

}

