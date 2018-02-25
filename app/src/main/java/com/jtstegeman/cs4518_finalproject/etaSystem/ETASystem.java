package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.awareness.state.Weather;
import com.jtstegeman.cs4518_finalproject.R;
import com.jtstegeman.cs4518_finalproject.etaSystem.learning.ETALearningSystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.learning.ExponentialMovingAverageLearner;
import com.jtstegeman.cs4518_finalproject.weather.WeatherManager;
import com.jtstegeman.cs4518_finalproject.weather.WeatherType;

/**
 * Created by kyle on 2/15/18.
 */

public class ETASystem {

    private ETAEstimator mETAEstimator;
    private ETALearningSystem mETALearner;
    private Context mContext;
    private SharedPreferences prefs;

    /**
     * A system which calculates the ETA time to a given location.
     * @param ETAEstimator
     */
    public ETASystem(ETAEstimator ETAEstimator, Context context) {
        mETAEstimator = ETAEstimator;
        this.mContext = context;
        if(context != null){
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
        this.mETALearner = new ETALearningSystem(new ExponentialMovingAverageLearner(0.6F), prefs);
    }

    /**
     * Set the ETAEstimator.
     * @param ETAEstimator The new ETAEstimator to be used.
     */
    public void setETAEstimator(ETAEstimator ETAEstimator) {
        mETAEstimator = ETAEstimator;
    }

    /**
     * Calculates the time it will take the user to travel from the current to the target location.
     * @param target The target location.
     * @param current The current location of the user.
     * @param activity The current activity of the user.
     * @return The time in seconds until the user arrives at the target location.
     */
    public int calculateTravelTime(Location target, Location current, UserActivity activity){
        if(current.hasSpeed() && activity != UserActivity.STATIONARY){
            mETALearner.learn(current.getSpeed(), activity);
        }
        int travelTime = mETAEstimator.calculateTravelTime(target, current, activity, prefs);
        WeatherType weather = WeatherManager.getInstance(mContext).getWeather(mContext);
        if(weather != null && prefs.getBoolean(mContext.getString(R.string.pref_use_weather_key), true)){
            travelTime *= weather.getTimeMultiplier();
        }
        return travelTime;
    }
}
