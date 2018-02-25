package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by kyle on 2/25/18.
 */

public class WeatherManager {

    private static WeatherManager instance;

    private WeatherType lastWeather;

    public WeatherManager(){
        lastWeather = WeatherType.CLEAR;
    }

    public synchronized static WeatherManager getInstance(){
        if(instance == null){
            instance = new WeatherManager();
        }
        return instance;
    }

    public WeatherType getCurrentWeather(){
        return lastWeather;
    }

    public void checkWeather(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Awareness.getSnapshotClient(context).getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
                @Override
                public void onComplete(@NonNull Task<WeatherResponse> task) {
                    WeatherResponse weatherResponse = task.getResult();
                    Weather weather = weatherResponse.getWeather();
                    lastWeather = retrieveConditionType(weather.getConditions().length != 0 ? weather.getConditions()[0] : Weather.CONDITION_CLEAR);
                    System.out.println(lastWeather);
                }
            });
        }
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
