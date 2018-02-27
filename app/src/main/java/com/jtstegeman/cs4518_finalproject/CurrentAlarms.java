package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jtstegeman.cs4518_finalproject.activityrecognition.ActivityRecognitionSystem;
import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETAFactory;
import com.jtstegeman.cs4518_finalproject.weather.WeatherManager;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CurrentAlarms extends AppCompatActivity {

    private AlarmAdapter mAdapter;
    private RecyclerView mAlarmRecyclerView;
    private UserActivity currentActivity;
    private Timer uiUpdateTimer;
    private TimerTask uiUpdateTask;

    protected void onCreate(Bundle savedInstanceState) {
        if (Settings.isFirst(this)) {
            Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
            finish();
        }
//        if(!BackgroundService.isRunning){
//            startService(new Intent(this, BackgroundService.class));
//        }
        ActivityRecognitionSystem.getInstance(this);
        UserLocation.getInstance(this).refresh(this);
        WeatherManager.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_alarms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Context ctx = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MakeAlarm.getMakeAlarmIntent(CurrentAlarms.this));
            }
        });
        requestSMS();

        mAlarmRecyclerView = this.findViewById(R.id.alarm_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getParent()));

        uiUpdateTimer = new Timer();
        uiUpdateTask = new TimerTask() {
            @Override
            public void run() {
                new UIUpdater().execute();
            }
        };
        uiUpdateTimer.scheduleAtFixedRate(uiUpdateTask, 0, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_alarms, menu);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestSMS() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    100);
        }
    }

    private void updateUI() {
        AlarmHelper alarmHelper = AlarmHelper.getInstance(this);
        List<AlarmObject> alarms = alarmHelper.getAlarms();


        if (mAdapter == null) {
            mAdapter = new AlarmAdapter(alarms);
            mAlarmRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setAlarms(alarms);
            mAdapter.notifyDataSetChanged();
        }


    }

    private class AlarmHolder extends RecyclerView.ViewHolder {

        private AlarmObject mAlarm;

        private TextView mName, mTime, mLocation, mETA;

        public AlarmHolder(View itemView) {

            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(TrackAlarm.getAlarmIntent(CurrentAlarms.this, mAlarm));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlarmScheduler.cancel(mAlarm, CurrentAlarms.this);
                    startActivity(MakeAlarm.getEditAlarmIntent(CurrentAlarms.this, mAlarm));
                    return true;
                }
            });

            mName = itemView.findViewById(R.id.eventName);
            mTime = itemView.findViewById(R.id.dataTime);
            mLocation = itemView.findViewById(R.id.dataLocation);
            mETA = itemView.findViewById(R.id.dataETA);
        }

        public void bindAlarm(AlarmObject alarm) {
            mAlarm = alarm;

            mName.setText(mAlarm.getName());

            int hours = mAlarm.getTime().getHours();
            String timeOfDay = "AM";
            if(hours > 12){
                hours %= 12;
                timeOfDay = "PM";
            } else if (hours == 0){
                hours = 12;
                timeOfDay = "AM";
            } else if (hours == 12){
                timeOfDay = "PM";
            }

            mTime.setText(String.format(getString(R.string.time_fs), hours, mAlarm.getTime().getMinutes(), timeOfDay));
            mLocation.setText(mAlarm.getLocation());

            Location mLocation = UserLocation.getLocation(CurrentAlarms.this);
            ETASystem mEstimator = ETAFactory.getDefaultETASystem(CurrentAlarms.this);
            Location destLocation = new Location("");
            final double mlatitude = alarm.getLatitude();
            final double mlongitude = alarm.getLongitude();
            destLocation.setLatitude(mlatitude);
            destLocation.setLongitude(mlongitude);
            currentActivity = ActivityRecognitionSystem.getInstance(CurrentAlarms.this).getActivity();//DetectedActivitiesIntentService.getCurrentActivity(CurrentAlarms.this);
            int ETA = mEstimator.calculateTravelTime(destLocation, mLocation, currentActivity);

            Time etaTime = new Time(ETA);

//            int hours = ETA / 3600;
//            int minutes = (ETA - hours * 3600) / 60;
//            int seconds = ETA - hours * 3600 - minutes * 60;

            String sETA = etaTime.getHours() + " hours " + etaTime.getMinutes() + " minutes " + etaTime.getSeconds() + " seconds";
            mETA.setText(sETA);
        }
    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmHolder> {

        private List<AlarmObject> mAlarms;

        public AlarmAdapter(List<AlarmObject> alarms) {
            mAlarms = alarms;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_alarm, parent, false);
            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            AlarmObject alarm = mAlarms.get(position);
            holder.bindAlarm(alarm);
        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }

        public void setAlarms(List<AlarmObject> alarms) {
            mAlarms = alarms;
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
