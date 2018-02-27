package com.jtstegeman.cs4518_finalproject;

/**
 * Created by jtste on 2/3/2018.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

import java.util.ArrayList;

public class DetectedActivitiesIntentService extends IntentService {

    private static UserActivity lastAcivity=null;

    public static final String INTENT_STR = "activity_intent";
    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
//        DetectedActivity best=null;
        DetectedActivity best = result.getMostProbableActivity();
//        for (DetectedActivity activity : detectedActivities) {
//            if ((best==null || best.getConfidence()<activity.getConfidence()) ){
//                if (activity.getType() == DetectedActivity.STILL){
//                    best = activity;
//                }
//                if (activity.getType() == DetectedActivity.WALKING && activity.getConfidence()>40){
//                    best = activity;
//                }
//                if (activity.getType() == DetectedActivity.RUNNING && activity.getConfidence()>40){
//                    best = activity;
//                }
//                if (activity.getType() == DetectedActivity.ON_BICYCLE && activity.getConfidence()>40){
//                    best = activity;
//                }
//            }
//        }
//        if(best != null && best.getConfidence() < 40){
//            best = null;
//        }
        if (best!=null)
            broadcastActivity(best);
    }

    private void broadcastActivity(DetectedActivity activity) {
        UserActivity lastKnownActivity = UserActivity.STATIONARY;
        if (activity.getType()==DetectedActivity.STILL){
            lastKnownActivity = UserActivity.STATIONARY;
        } else if (activity.getType()==DetectedActivity.WALKING){
            lastKnownActivity = UserActivity.WALKING;
        } else if (activity.getType()==DetectedActivity.RUNNING){
            lastKnownActivity = UserActivity.RUNNING;
        } else if (activity.getType()==DetectedActivity.IN_VEHICLE){
            lastKnownActivity = UserActivity.DRIVING;
        } else if (activity.getType()==DetectedActivity.ON_BICYCLE){
            lastKnownActivity = UserActivity.BIKING;
        }
        setCurrentActivity(this, lastKnownActivity);
        Intent intent = new Intent(INTENT_STR);
        intent.putExtra("type", activity.getType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void setCurrentActivity(Context ctx, UserActivity activity){
        lastAcivity = activity;
        SharedPreferences settings = ctx.getSharedPreferences("App", Context.MODE_PRIVATE);
        settings.edit().putString("curSpeed", activity.name());
        Log.i("DetectedActivity", "Activity: " + activity);
    }

    public static UserActivity getCurrentActivity(Context ctx){
        if (lastAcivity!=null){
            return lastAcivity;
        }
        SharedPreferences settings = ctx.getSharedPreferences("App", Context.MODE_PRIVATE);
        lastAcivity = UserActivity.valueOf(settings.getString("curSpeed","STATIONARY"));
        return lastAcivity;
    }
}