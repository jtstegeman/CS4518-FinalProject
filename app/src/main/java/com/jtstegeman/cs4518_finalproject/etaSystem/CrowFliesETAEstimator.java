package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.content.SharedPreferences;
import android.location.Location;

import com.jtstegeman.cs4518_finalproject.Settings;

/**
 * Created by kyle on 2/15/18.
 */

public class CrowFliesETAEstimator implements ETAEstimator {
    @Override
    public int calculateTravelTime(Location target, Location current, UserActivity activity, SharedPreferences preferences) {
        double distance = current.distanceTo(target) * Math.PI / 2.0; // Used to estimate non-linear distance within 2 standard deviations


        if (distance >= 1800 && preferences.getBoolean(ETAPrefs.IGNORE_ACTIVITY_LONG_DISTANCE, true)) {
            if (distance < 2000){
                activity = UserActivity.RUNNING;
            } else if (distance < 15000){ // There is probably a better algorithm
                activity = UserActivity.BIKING;
            } else {
                activity = UserActivity.DRIVING;
            }
        }


        double speedMetersPerSecond;

        switch (activity){
            case WALKING:
                speedMetersPerSecond = preferences.getFloat(ETAPrefs.WALKING_SPEED_PREF, UserActivity.WALKING.getDefaultSpeed());
                break;
            case RUNNING:
                speedMetersPerSecond = preferences.getFloat(ETAPrefs.RUNNING_SPEED_PREF, UserActivity.RUNNING.getDefaultSpeed());
                break;
            case BIKING:
                speedMetersPerSecond = preferences.getFloat(ETAPrefs.BIKING_SPEED_PREF, UserActivity.BIKING.getDefaultSpeed());
                break;
            case DRIVING:
                speedMetersPerSecond = preferences.getFloat(ETAPrefs.DRIVING_SPEED_PREF, UserActivity.DRIVING.getDefaultSpeed());
                break;
            default:
                speedMetersPerSecond = UserActivity.STATIONARY.getDefaultSpeed();
        }
        return speedMetersPerSecond == 0 ? Integer.MAX_VALUE : (int) Math.round(distance / speedMetersPerSecond);
    }
}
