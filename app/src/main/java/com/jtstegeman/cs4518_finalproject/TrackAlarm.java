package com.jtstegeman.cs4518_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;
import com.jtstegeman.cs4518_finalproject.etaSystem.CrowFliesETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETAFactory;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;
import com.jtstegeman.cs4518_finalproject.weather.WeatherManager;
import com.jtstegeman.cs4518_finalproject.weather.WeatherType;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TrackAlarm extends AppCompatActivity {

    public static final String EXTRA_ALARM_NAME = "alarm_name";

    private UserActivity currentActivity;


    private TextView mETATextView;
    private TextView mLocationTextView;
    private TextView mActionTextView;
    private TextView mWeatherTextView;
    private ImageView weatherImage;
    private ImageView activityImage;


    private Button  mGoogleMapsButton;

    private Timer timer;

    private AlarmObject alarm;



    public static Intent getAlarmIntent(Context context, AlarmObject alarm) {
        Intent intent = new Intent(context, TrackAlarm.class);
        intent.putExtra(EXTRA_ALARM_NAME, alarm.getName());
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_alarm);

        mETATextView = findViewById(R.id.ETA_textview);
        mLocationTextView = findViewById(R.id.Location_textview);
        mActionTextView = findViewById(R.id.Action_textview);
        mGoogleMapsButton = findViewById(R.id.google_maps_button);
        mWeatherTextView = findViewById(R.id.Weather_textview);
        weatherImage = findViewById(R.id.weatherImage);
        activityImage = findViewById(R.id.activityImage);

        timer = new Timer();

        Intent seed = getIntent();

        alarm = AlarmHelper.getInstance(this).get(seed.getStringExtra(EXTRA_ALARM_NAME));

        getSupportActionBar().setTitle(alarm.getName());

        final double mlatitude = alarm.getLatitude();
        final double mlongitude = alarm.getLongitude();


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

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               new UIUpdater().execute();
            }
        }, 0, 1000);
    }

    private void updateUI(){
        Location mLocation = UserLocation.getLocation(TrackAlarm.this);
        ETASystem mEstimator = ETAFactory.getDefaultETASystem(TrackAlarm.this);
        Location destLocation = new Location("");
        final double mlatitude = alarm.getLatitude();
        final double mlongitude = alarm.getLongitude();
        destLocation.setLatitude(mlatitude);
        destLocation.setLongitude(mlongitude);
        currentActivity = ActivityRecognitionSystem.getInstance(this).getActivity();

        int ETA = mEstimator.calculateTravelTime(destLocation, mLocation, currentActivity);

        Time time = new Time(ETA);

        String sETA = time.getHours() + " hours " + time.getMinutes() + " minutes "  + time.getSeconds() + " seconds ";

        mETATextView.setText(sETA);
        mLocationTextView.setText(alarm.getLocation());

        switch(currentActivity){
            case BIKING:
                mActionTextView.setText("Biking");
                activityImage.setImageDrawable(getDrawable(R.drawable.ic_directions_bike_black_24dp));
                break;
            case DRIVING:
                mActionTextView.setText("Driving");
                activityImage.setImageDrawable(getDrawable(R.drawable.ic_drive_eta_black_24dp));
                break;
            case RUNNING:
                mActionTextView.setText("Running");
                activityImage.setImageDrawable(getDrawable(R.drawable.ic_directions_run_black_24dp));
                break;
            case WALKING:
                mActionTextView.setText("Walking");
                activityImage.setImageDrawable(getDrawable(R.drawable.ic_directions_walk_black_24dp));
                break;
            case STATIONARY:
                mActionTextView.setText("Stationary");
                activityImage.setImageDrawable(getDrawable(R.drawable.ic_sofa_black_24dp));
                break;
        }

        WeatherType weather = WeatherManager.getInstance(TrackAlarm.this).getWeather(TrackAlarm.this);

        switch (weather){
            case RAIN:
                mWeatherTextView.setText(R.string.raining_out);
                weatherImage.setImageDrawable(getDrawable(R.drawable.ic_weather_rainy_black_24dp));
                break;
            case SNOW:
                mWeatherTextView.setText(R.string.snowing_out);
                weatherImage.setImageDrawable(getDrawable(R.drawable.ic_weather_snowy_black_24dp));
                break;
            default:
                mWeatherTextView.setText(R.string.clear_out);
                if(WeatherManager.getInstance(TrackAlarm.this).isNight()){
                    weatherImage.setImageDrawable(getDrawable(R.drawable.ic_weather_night_black_24dp));
                } else {
                    weatherImage.setImageDrawable(getDrawable(R.drawable.ic_weather_sunny_black_24dp));
                }
                break;
        }
    }

    private class UIUpdater extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }
}
