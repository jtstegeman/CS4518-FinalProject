package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.location.Location;

/**
 * Created by kyle on 2/15/18.
 */

public class ETASystem {

    private ETAEstimator mETAEstimator;

    /**
     * A system which calculates the ETA time to a given location.
     * @param ETAEstimator
     */
    public ETASystem(ETAEstimator ETAEstimator) {
        mETAEstimator = ETAEstimator;
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
        return mETAEstimator.calculateTravelTime(target, current, activity);
    }
}
