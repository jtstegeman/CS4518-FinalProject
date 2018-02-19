package com.jtstegeman.cs4518_finalproject.etaSystem;

import android.location.Location;

/**
 * Created by kyle on 2/15/18.
 */

public class CrowFliesETAEstimator implements ETAEstimator {
    @Override
    public int calculateTravelTime(Location target, Location current, UserActivity activity) {
        double distance = current.distanceTo(target) * Math.PI / 2.0; // Used to estimate non-linear distance within 2 standard deviations
        double speedMetersPerSecond;
        switch (activity){
            case WALKING:
                speedMetersPerSecond = 1.4;
                break;
            case RUNNING:
                speedMetersPerSecond = 3;
                break;
            case BIKING:
                speedMetersPerSecond = 10;
                break;
            case DRIVING:
                speedMetersPerSecond = 20;
                break;
            default:
                speedMetersPerSecond = 0;
        }
        if(target.getSpeed() != 0){
            speedMetersPerSecond = target.getSpeed();
        }
        return speedMetersPerSecond == 0 ? Integer.MAX_VALUE : (int) Math.round(distance / speedMetersPerSecond);
    }
}
