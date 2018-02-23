package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.content.SharedPreferences;
import android.location.Location;

import com.jtstegeman.cs4518_finalproject.etaSystem.learning.ETALearningSystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.learning.ExponentialMovingAverageLearner;

/**
 * Created by kyle on 2/15/18.
 */

public class ETASystem {

    private ETAEstimator mETAEstimator;
    private ETALearningSystem mETALearner;
    private SharedPreferences prefs;

    /**
     * A system which calculates the ETA time to a given location.
     * @param ETAEstimator
     */
    public ETASystem(ETAEstimator ETAEstimator, SharedPreferences prefs) {
        mETAEstimator = ETAEstimator;
        this.prefs = prefs;
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
        return mETAEstimator.calculateTravelTime(target, current, activity, prefs);
    }
}
