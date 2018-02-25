package com.jtstegeman.cs4518_finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MakeAlarm extends AppCompatActivity {
    EditText eventName, locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventName = findViewById(R.id.newName);
        locationName = findViewById(R.id.newLocation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
            }
        });
        picker.show(getFragmentManager(), "datePicker");
    }

    public void selectNewTime(View v) {
        TimePickerFragment picker = new TimePickerFragment();
        picker.setListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                Toast.makeText(MakeAlarm.this, String.format("The time is %d:%d", hourOfDay, minute), Toast.LENGTH_LONG).show();
            }
        });
        picker.show(getFragmentManager(), "timePicker");
    }

    public void cancelNewAlarm(View v) {
        finish();
    }
    public void confirmNewAlarm(View v) {

    }

}

