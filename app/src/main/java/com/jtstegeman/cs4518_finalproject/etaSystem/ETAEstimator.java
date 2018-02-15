package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.location.Location;

/**
 * Created by kyle on 2/15/18.
 */

public interface ETAEstimator {

    /**
     * Calculates the time it will take the user to travel from the current to the target location.
     * @param target The target location.
     * @param current The current location of the user.
     * @param activity The current activity of the user.
     * @return The time in seconds until the user arrives at the target location.
     */
    int calculateTravelTime(Location target, Location current, UserActivity activity);

}
