package com.jtstegeman.cs4518_finalproject.weather;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.snapshot.TimeIntervalsResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kyle on 2/25/18.
 */

public class WeatherManager {

    private Context mContext;

    private static WeatherManager instance;

    private WeatherType lastWeather = null;
    private boolean isNight = false;

    public WeatherManager(final Context context){
        mContext = context;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkWeather(mContext);
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000 * 60 * 10); // Schedule every 5 minutes
    }

    public synchronized static WeatherManager getInstance(final Context context){
        if(instance == null){
            instance = new WeatherManager(context);
        }
        return instance;
    }

    public WeatherType getWeather(final Context context){
        if(lastWeather != null){
            return lastWeather;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int weatherID = prefs.getInt("weather", 0);
        lastWeather = WeatherType.fromID(weatherID);
        return lastWeather;
    }

    private void checkWeather(final Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Awareness.getSnapshotClient(context).getTimeIntervals().addOnCompleteListener(new OnCompleteListener<TimeIntervalsResponse>() {
                @Override
                public void onComplete(@NonNull Task<TimeIntervalsResponse> task) {
                    TimeIntervalsResponse response = task.getResult();
                    TimeIntervals timeIntervals = response.getTimeIntervals();
                    int[] intervals = timeIntervals.getTimeIntervals();

                    boolean found = false;
                    for(int timeInterval: intervals){
                        if(timeInterval == TimeFence.TIME_INTERVAL_NIGHT){
                            found = true;
                            break;
                        }
                    }

                    setNight(found);

                    Log.i("WeatherManager", "Time of day: " + (isNight ? "night" : "day"));
                }
            });
            Awareness.getSnapshotClient(context).getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
                @Override
                public void onComplete(@NonNull Task<WeatherResponse> task) {
                    WeatherResponse weatherResponse = task.getResult();
                    Weather weather = weatherResponse.getWeather();
                    setWeather(retrieveConditionType(weather.getConditions().length != 0 ? weather.getConditions()[0] : Weather.CONDITION_CLEAR), context);
                    Log.i("WeatherManager", "Weather: " + lastWeather);
                }
            });
        }
    }

    private void setNight(boolean isNight){
        this.isNight = isNight;
    }

    public boolean isNight(){
        return isNight;
    }

    private void setWeather(WeatherType weather, Context context){
        lastWeather = weather;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt("weather", weather.getId()).apply();
    }


    private WeatherType retrieveConditionType(int condition) {
        switch (condition) {
            case Weather.CONDITION_ICY:
            case Weather.CONDITION_SNOWY:
               return WeatherType.SNOW;
            case Weather.CONDITION_RAINY:
            case Weather.CONDITION_STORMY:
               return WeatherType.RAIN;
            default:
               return WeatherType.CLEAR;
        }
    }
}
