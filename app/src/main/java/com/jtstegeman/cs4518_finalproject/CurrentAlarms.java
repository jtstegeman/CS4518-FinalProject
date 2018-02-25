package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;
import com.jtstegeman.cs4518_finalproject.etaSystem.CrowFliesETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

import java.util.Date;
import java.util.List;

public class CurrentAlarms extends AppCompatActivity {


    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private AlarmAdapter mAdapter;
    private RecyclerView mAlarmRecyclerView;
    private UserActivity currentActivity;

    private boolean mSubtitleVisible;


    protected void onCreate(Bundle savedInstanceState) {
        if(Settings.isFirst(this)){
            Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
            finish();
        }
        UserLocation.getInstance(this).refresh(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_alarms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Context ctx = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MakeAlarm.getMakeAlarmIntent(CurrentAlarms.this));
            }
        });
        requestSMS();




        mAlarmRecyclerView = (RecyclerView) this
                .findViewById(R.id.alarm_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getParent()));


        updateUI();
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
        updateUI();
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

    private class AlarmHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private AlarmObject mAlarm;

        private TextView mTime;
        private TextView mLocation;
        private TextView mETA;

        public AlarmHolder(View itemView){

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
                    startActivity(MakeAlarm.getEditAlarmIntent(CurrentAlarms.this, mAlarm));
                    return true;
                }
            });



            mTime = (TextView) itemView.findViewById(R.id.dataTime);
            mLocation = (TextView) itemView.findViewById(R.id.dataLocation);
            mETA = (TextView) itemView.findViewById(R.id.dataETA);
        }

        public void bindAlarm(AlarmObject alarm){
            mAlarm = alarm;

            mTime.setText(mAlarm.getTime().toString());
            mLocation.setText(mAlarm.getLocation());

            Location mLocation = UserLocation.getLocation(CurrentAlarms.this);
            CrowFliesETAEstimator mCrowEstimator = new CrowFliesETAEstimator();
            SharedPreferences settings = CurrentAlarms.this.getSharedPreferences("App", Context.MODE_PRIVATE);
            ETASystem mEstimator = new ETASystem(mCrowEstimator, settings);
            Location destLocation = new Location("");
            final double mlatitude = alarm.getLatitude();
            final double mlongitude = alarm.getLongitude();
            destLocation.setLatitude(mlatitude);
            destLocation.setLongitude(mlongitude);
            currentActivity = DetectedActivitiesIntentService.getCurrentActivity(CurrentAlarms.this);
            int ETA = mEstimator.calculateTravelTime(destLocation, mLocation, currentActivity);


            int hours = ETA/3600;
            int minutes = (ETA - hours*3600)/60;
            int seconds = ETA - hours*3600 - minutes*60;

            String sETA = hours + " hours " + minutes + " minutes " + seconds + " seconds";
            mETA.setText(sETA);
        }

        @Override
        public void onClick(View v) {
           // Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
           // startActivity(intent);
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
}
