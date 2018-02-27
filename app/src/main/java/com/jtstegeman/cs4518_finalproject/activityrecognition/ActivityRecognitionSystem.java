package com.jtstegeman.cs4518_finalproject.activityrecognition;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by kyle on 2/27/18.
 */

public class ActivityRecognitionSystem {

    private static ActivityRecognitionSystem instance;
    private UserActivity lastActivity;

    private final String PERSISTENT_ACTIVITY_KEY = "activity";

    private ActivityRecognitionSystem(final Context context){
        checkActivity(context);
    }

    public static ActivityRecognitionSystem getInstance(final Context context){
        if(instance == null){
            instance = new ActivityRecognitionSystem(context);
        }

        return instance;
    }

    private void checkActivity(Context context){
        ActivityRecognitionClient mActivityRecognitionClient = new ActivityRecognitionClient(context);
        Intent mIntent = new Intent(context, ActivityIntentService.class);
        PendingIntent mPendingIntent = PendingIntent.getService(context, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(3000, mPendingIntent);
    }

    public void setActivity(UserActivity activity, Context context){
        lastActivity = activity;
        Log.i("ActivityRecognition", "Activity: " + lastActivity);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PERSISTENT_ACTIVITY_KEY, activity.name()).apply();
    }

    public UserActivity getActivity(Context context){
        if(lastActivity != null){
            return lastActivity;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return UserActivity.valueOf(preferences.getString(PERSISTENT_ACTIVITY_KEY, UserActivity.STATIONARY.name()));
    }




}
