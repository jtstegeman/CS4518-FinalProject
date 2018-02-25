package com.jtstegeman.cs4518_finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;

import java.util.Calendar;
import java.util.Locale;

public class MakeAlarm extends AppCompatActivity {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_ALARM_NAME = "alarm_name";
    public static final int MODE_MAKE = 1;
    public static final int MODE_EDIT = 2;

    private int mode;
    private Calendar calendar;

    private Button newDate, newTime;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();

        newDate = findViewById(R.id.newDate);
        newTime = findViewById(R.id.newTime);
        eventName = findViewById(R.id.newName);
        locationName = findViewById(R.id.newLocation);

        Intent seed = getIntent();
        if ((mode = seed.getIntExtra(EXTRA_MODE, MODE_MAKE)) == MODE_EDIT) {
            AlarmObject alarm = AlarmHelper.getInstance(this).get(seed.getStringExtra(EXTRA_ALARM_NAME));
            eventName.setText(alarm.getName());
            eventName.setEnabled(false);
            locationName.setText(alarm.getLocation());
            calendar.setTime(alarm.getTime());
            toolbar.setTitle(R.string.title_activity_make_alarm_alternative);
            findViewById(R.id.action_delete).setVisibility(View.VISIBLE);
            updateNewDate();
            updateNewTime();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void selectNewLocation(View v) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void selectNewDate(View v) {
        DatePickerFragment picker = new DatePickerFragment();
        picker.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateNewDate();
            }
        });
        picker.show(getFragmentManager(), "datePicker");
    }

    public void selectNewTime(View v) {
        TimePickerFragment picker = new TimePickerFragment();
        picker.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateNewTime();
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
        if (mode == MODE_MAKE) {
            AlarmHelper.getInstance(this).create(alarm);
        } else if (mode == MODE_EDIT) {
            AlarmHelper.getInstance(this).update(alarm);
        }
        finish();
    }

    private void updateNewDate() {
        newDate.setText(String.format(getString(R.string.select_date_fs),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US),
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
    }

    private void updateNewTime() {
        newTime.setText(String.format(getString(R.string.select_time_fs),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }

}

