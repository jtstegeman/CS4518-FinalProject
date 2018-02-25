package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MakeAlarm extends AppCompatActivity {
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_ALARM_NAME = "alarm_name";
    public static final int MODE_MAKE = 1;
    public static final int MODE_EDIT = 2;
    private static final int PLACE_PICKER_REQUEST = 1;

    private int mode;
    private Calendar calendar;

    private Button newDate, newTime, newLocation;
    private EditText eventName, locationName, phoneNumbersEditText;

    Location current = UserLocation.getLocation(this);
    private double lat = current.getLatitude();
    private double lon = current.getLongitude();

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
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        newDate = findViewById(R.id.newDate);
        newTime = findViewById(R.id.newTime);
        newLocation = findViewById(R.id.newGPSLocation);
        eventName = findViewById(R.id.newName);
        locationName = findViewById(R.id.newLocation);
        phoneNumbersEditText = findViewById(R.id.phoneNumbers);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_sms_blast_key), false)) {
            phoneNumbersEditText.setEnabled(false);
        }

        Intent seed = getIntent();
        if ((mode = seed.getIntExtra(EXTRA_MODE, MODE_MAKE)) == MODE_EDIT) {
            AlarmObject alarm = AlarmHelper.getInstance(this).get(seed.getStringExtra(EXTRA_ALARM_NAME));
            eventName.setText(alarm.getName());
            eventName.setEnabled(false);
            locationName.setText(alarm.getLocation());
            calendar.setTime(alarm.getTime());
            phoneNumbersEditText.setText(formatPhoneNumbers(alarm.getPhoneNumbers()));
            lat = alarm.getLatitude();
            lon = alarm.getLongitude();
            getSupportActionBar().setTitle(R.string.title_activity_make_alarm_alternative);
            updateNewDate();
            updateNewTime();
            updateNewLocation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make_alarm, menu);
        if (mode == MODE_EDIT) menu.findItem(R.id.action_delete).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_cancel:
                finish();
                return true;
            case R.id.action_save:
                confirmNewAlarm();
                return true;
            case R.id.action_delete:
                deleteAlarm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectNewLocation(View v) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        double defaultZoomConstant = 0.006/2;
        builder.setLatLngBounds(new LatLngBounds(
                new LatLng(lat-defaultZoomConstant,lon-defaultZoomConstant),
                new LatLng(lat+defaultZoomConstant, lon+defaultZoomConstant)));

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                updateNewLocation();
            }
        }
    }

    public void selectNewDate(View v) {
        DatePickerFragment picker = new DatePickerFragment(calendar, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerFragment picker = new TimePickerFragment(calendar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateNewTime();
            }
        });
        picker.show(getFragmentManager(), "timePicker");
    }

    private void confirmNewAlarm() {
        AlarmObject alarm = new AlarmObject(eventName.getText().toString());
        alarm.setLocation(locationName.getText().toString());
        alarm.setTime(calendar.getTime());
        alarm.setLatitude(lat);
        alarm.setLongitude(lon);
        alarm.setPhoneNumbers(getPhoneNumbers());
        if (mode == MODE_MAKE) {
            AlarmHelper.getInstance(this).create(alarm);
        } else if (mode == MODE_EDIT) {
            AlarmHelper.getInstance(this).update(alarm);
            AlarmScheduler.cancel(alarm, this);
        }
        AlarmScheduler.schedule(alarm, this);
        finish();
    }

    private void deleteAlarm() {
        AlarmHelper.getInstance(this).delete(new AlarmObject(eventName.getText().toString()));
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

    private Collection<String> getPhoneNumbers(){
        String text = phoneNumbersEditText.getText().toString();
        List<String> phoneNumbers = new LinkedList<>();
        String[] nums = text.split(", *");
        for (String num: nums){
            if(isPhoneNumber(num)) {
                phoneNumbers.add(formatPhoneNumber(num));
            }
        }
        return phoneNumbers;
    }

    private String formatPhoneNumber(String phoneNumber){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (phoneNumber.charAt(i) >= '0' && phoneNumber.charAt(i) <= '9'){
                stringBuilder.append(phoneNumber.charAt(i));
            }
        }
        return stringBuilder.toString();
    }

    private boolean isPhoneNumber(String phoneNumber){
        int digitCount = 0;
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (phoneNumber.charAt(i) >= '0' && phoneNumber.charAt(i) <= '9'){
                digitCount++;
            }
        }

        return (digitCount == 10 || digitCount == 11 || digitCount == 7);

    }

    private String formatPhoneNumbers(Collection<String> phoneNumbers) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = phoneNumbers.iterator();
        while (iterator.hasNext()) {
            String phoneNum = iterator.next();
            stringBuilder.append(phoneNum);
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private void updateNewLocation() {
        newLocation.setText(String.format(getString(R.string.select_location_fs), lat, lon));
    }

}

