package com.jtstegeman.cs4518_finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kyle on 2/23/18.
 */

public class Settings {

    public static boolean isFirst(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_first_time_key), true);
    }

    public static void setFirst(Context context, boolean isFirst){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_first_time_key), isFirst);
        editor.apply();
    }

    public static boolean shouldDisregardActivityOverLongDistances(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_disregard_long_distances_key), true);
    }

    public static void setDisregardActivityOverLongDistances(Context context, boolean disregard){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_disregard_long_distances_key), disregard);
        editor.apply();
    }

}
