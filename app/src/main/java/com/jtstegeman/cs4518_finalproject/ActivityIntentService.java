package com.jtstegeman.cs4518_finalproject;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by kyle on 2/27/18.
 */

public class ActivityIntentService extends IntentService{

    public ActivityIntentService() {
        super("ActivityIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        DetectedActivity activity = result.getMostProbableActivity();
        UserActivity lastKnownActivity = UserActivity.STATIONARY;
        if (activity.getType()==DetectedActivity.STILL){
            lastKnownActivity = UserActivity.STATIONARY;
        } else if (activity.getType()==DetectedActivity.WALKING || activity.getType() == DetectedActivity.ON_FOOT){
            lastKnownActivity = UserActivity.WALKING;
        } else if (activity.getType()==DetectedActivity.RUNNING){
            lastKnownActivity = UserActivity.RUNNING;
        } else if (activity.getType()==DetectedActivity.IN_VEHICLE){
            lastKnownActivity = UserActivity.DRIVING;
        } else if (activity.getType()==DetectedActivity.ON_BICYCLE){
            lastKnownActivity = UserActivity.BIKING;
        }
        ActivityRecognitionSystem.getInstance(this).setActivity(lastKnownActivity);
    }
}
