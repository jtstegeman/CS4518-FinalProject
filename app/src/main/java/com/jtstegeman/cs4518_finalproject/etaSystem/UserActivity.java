package com.jtstegeman.cs4518_finalproject.etaSystem;

/**
 * Created by kyle on 2/15/18.
 */

public enum UserActivity {

    WALKING(1.4F),
    RUNNING(3.0F),
    STATIONARY(1.1F),
    BIKING(10.0F),
    DRIVING(20.0F);


    private float defaultSpeed;

    UserActivity(float defaultSpeed) {
        this.defaultSpeed = defaultSpeed;
    }

    public float getDefaultSpeed() {
        return defaultSpeed;
    }
}
