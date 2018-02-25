package com.jtstegeman.cs4518_finalproject.weather;

/**
 * Created by kyle on 2/25/18.
 */

public enum WeatherType {
    CLEAR(0, 1),
    RAIN(1, 1.2),
    SNOW(2, 1.3);

    private int id;
    private double timeMultiplier;

    WeatherType(int id, double timeMultiplier) {
        this.id = id;
        this.timeMultiplier = timeMultiplier;
    }

    public int getId() {
        return id;
    }

    public double getTimeMultiplier() {
        return timeMultiplier;
    }

    public static WeatherType fromID(int id){
        switch (id){
            case 0:
                return CLEAR;
            case 1:
                return RAIN;
            case 2:
                return SNOW;
            default:
                return CLEAR;
        }
    }
}

