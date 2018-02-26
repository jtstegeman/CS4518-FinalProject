package com.jtstegeman.cs4518_finalproject;

/**
 * Created by kyle on 2/26/18.
 */

public class Time {

    private int seconds;

    public Time(int seconds) {
        this.seconds = seconds;
    }


    private void setTime(int seconds){
        this.seconds = seconds;
    }

    public int getHours(){
        if(seconds < 0){
            return 0;
        }
        return seconds / (60 * 60);
    }


    public int getMinutes(){
        if(seconds < 0){
            return 0;
        }

        return (seconds % (60 * 60))/ 60;

    }

    public int getSeconds(){
        if(seconds < 0){
            return 0;
        }

        return seconds % 60;

    }

}
