package com.jtstegeman.cs4518_finalproject;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MakeAlarm extends AppCompatActivity {

    Button selectLocation, selectTime, cancel, confirm;
    EditText eventName, locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventName = findViewById(R.id.newName);
        locationName = findViewById(R.id.newLocation);

        selectLocation = findViewById(R.id.newGPSLocation);
        selectTime = findViewById(R.id.newTime);
        cancel = findViewById(R.id.newCancel);
        confirm = findViewById(R.id.newConfirm);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void selectNewLocation(View v){

    }

    public void selectNewDate(View v){
        //TODO: Add code from CriminalIntent project for bringing up the date selector

    }

    public void cancelNewAlarm(View v){

    }
    public void confirmNewAlarm(View v){

    }

}

