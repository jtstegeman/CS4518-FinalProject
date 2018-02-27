package com.jtstegeman.cs4518_finalproject;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by kyle on 2/27/18.
 */

public class ActivityRecognitionSystem {

    private Context mContext;
    private static ActivityRecognitionSystem instance;
    private UserActivity lastActivity;

    private final String PERSISTENT_ACTIVITY_KEY = "activity";

    private ActivityRecognitionSystem(final Context context){
        mContext = context;
        checkActivity();
    }

    public static ActivityRecognitionSystem getInstance(final Context context){
        if(instance == null){
            instance = new ActivityRecognitionSystem(context);
        }

        if(instance.mContext == null){
            instance.mContext = context;
        }

        return instance;
    }

    private void checkActivity(){
        ActivityRecognitionClient mActivityRecognitionClient = new ActivityRecognitionClient(mContext);
        Intent mIntent = new Intent(mContext, ActivityIntentService.class);
        PendingIntent mPendingIntent = PendingIntent.getService(mContext, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(3000, mPendingIntent);
    }

    public void setActivity(UserActivity activity){
        lastActivity = activity;
        Log.i("ActivityRecognition", "Activity: " + lastActivity);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.edit().putString(PERSISTENT_ACTIVITY_KEY, activity.name()).apply();
    }

    public UserActivity getActivity(){
        if(lastActivity != null){
            return lastActivity;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return UserActivity.valueOf(preferences.getString(PERSISTENT_ACTIVITY_KEY, UserActivity.STATIONARY.name()));
    }




}
