package com.jtstegeman.cs4518_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;
import com.jtstegeman.cs4518_finalproject.etaSystem.CrowFliesETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

public class TrackAlarm extends AppCompatActivity {

    public static final String EXTRA_ALARM_NAME = "alarm_name";

    private UserActivity currentActivity;


    private TextView mETATextView;
    private TextView mLocationTextView;
    private TextView mActionTextView;


    private Button  mGoogleMapsButton;



    public static Intent getAlarmIntent(Context context, AlarmObject alarm) {
        Intent intent = new Intent(context, TrackAlarm.class);
        intent.putExtra(EXTRA_ALARM_NAME, alarm.getName());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_alarm);

        mETATextView = (TextView) findViewById(R.id.ETA_textview);
        mLocationTextView = (TextView) findViewById(R.id.Location_textview);
        mActionTextView = (TextView) findViewById(R.id.Action_textview);
        mGoogleMapsButton = (Button) findViewById(R.id.google_maps_button);

        Intent seed = getIntent();

        AlarmObject alarm = AlarmHelper.getInstance(this).get(seed.getStringExtra(EXTRA_ALARM_NAME));

        Location mLocation = UserLocation.getLocation(this);
        CrowFliesETAEstimator mCrowEstimator = new CrowFliesETAEstimator();
        SharedPreferences settings = this.getSharedPreferences("App", Context.MODE_PRIVATE);
        ETASystem mEstimator = new ETASystem(mCrowEstimator, settings);
        Location destLocation = new Location("");
        final double mlatitude = alarm.getLatitude();
        final double mlongitude = alarm.getLongitude();
        destLocation.setLatitude(mlatitude);
        destLocation.setLongitude(mlongitude);
        currentActivity = DetectedActivitiesIntentService.getCurrentActivity(this);
        int ETA = mEstimator.calculateTravelTime(destLocation, mLocation, currentActivity);

        mETATextView.setText(String.valueOf(ETA));
        mLocationTextView.setText(alarm.getLocation());

        switch(currentActivity){
            case BIKING:
                mActionTextView.setText("Biking");
                break;
            case DRIVING:
                mActionTextView.setText("Driving");
                break;
            case RUNNING:
                mActionTextView.setText("Running");
                break;
            case WALKING:
                mActionTextView.setText("Walking");
                break;
            case STATIONARY:
                mActionTextView.setText("Stationary");
                break;
        }

        mGoogleMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude) +"&mode=w";

                switch(currentActivity){
                    case BIKING:
                        UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude) +"&mode=b";
                        break;
                    case DRIVING:
                        UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude);
                        break;
                    case RUNNING:
                        UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude) +"&mode=w";
                        break;
                    case WALKING:
                        UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude) +"&mode=w";
                        break;
                    case STATIONARY:
                        UriString = "google.navigation:q=" + String.valueOf(mlatitude) + "," + String.valueOf(mlongitude) +"&mode=w";
                        break;
                }

                Uri mUri = Uri.parse(UriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


    }
}
