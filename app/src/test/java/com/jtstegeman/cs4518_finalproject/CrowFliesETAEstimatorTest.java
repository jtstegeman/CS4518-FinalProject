package com.jtstegeman.cs4518_finalproject;

import android.location.Location;

import com.jtstegeman.cs4518_finalproject.etaSystem.CrowFliesETAEstimator;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kyle on 2/15/18.
 */

public class CrowFliesETAEstimatorTest {

    private ETASystem mETASystem;

    @Before
    public void setup(){
        mETASystem = new ETASystem(new CrowFliesETAEstimator());
    }

    @Test
    public void testTravelTime(){
        Location dunkin = new Location("");
        dunkin.setLatitude(42.2747862);
        dunkin.setLongitude(-71.8085448);

        Location founders = new Location("");
        founders.setLatitude(42.2731908);
        founders.setLongitude(-71.8052753);

        double distance = 321.59;

        double speed = 1.4;

        int time = (int) Math.round(distance * speed);

        assertEquals(time, mETASystem.calculateTravelTime(dunkin, founders, UserActivity.WALKING));

    }

}
