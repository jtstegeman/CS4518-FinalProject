package com.jtstegeman.cs4518_finalproject.database;

import java.util.Date;

/**
 * Created by jtste on 2/15/2018.
 */

public class AlarmObject {
    public static final String DB_ALARM_TABLE = "alarms";
    public static final String DB_NAME = "name";
    public static final String DB_LAT = "lat";
    public static final String DB_LNG = "lng";
    public static final String DB_LOCATION = "loc";
    public static final String DB_TIME = "time";
    public static final String DB_EXTRA_TIME = "buftime";

    private String name = "Default Name";
    private double latitude = 42.274456;
    private double longitude = -71.806722;
    private String location = "";
    private Date time = new Date(System.currentTimeMillis()+60000*60*24);
    private long bufferTime = 5;

    public AlarmObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getBufferTime_ms() {
        return bufferTime;
    }

    public long getBufferTime_min() {
        return bufferTime/60000;
    }

    public void setBufferTime_ms(long bufferTime) {
        this.bufferTime = bufferTime;
    }
    public void setBufferTime_min(long bufferTime) {
        this.bufferTime = bufferTime*60000;
    }

    public Date getDesiredArrivalTime() {
        return new Date(time.getTime()-this.getBufferTime_ms());
    }

    public boolean setDesiredArrivalTime(Date arrival) {
        if (arrival.getTime()>this.time.getTime()){
            return false;
        }
        this.bufferTime = this.time.getTime()-arrival.getTime();
        return true;
    }
}
