package com.jtstegeman.cs4518_finalproject;

/**
 * Created by jtste on 2/3/2018.
 */

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class DetectedActivitiesIntentService extends IntentService {

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
        DetectedActivity best=null;
        for (DetectedActivity activity : detectedActivities) {
            if ((best==null || best.getConfidence()<activity.getConfidence()) ){
                if (activity.getType() == DetectedActivity.STILL){
                    best = activity;
                }
                if (activity.getType() == DetectedActivity.WALKING && activity.getConfidence()>40){
                    best = activity;
                }
                if (activity.getType() == DetectedActivity.RUNNING && activity.getConfidence()>40){
                    best = activity;
                }
                if (activity.getType() == DetectedActivity.ON_BICYCLE && activity.getConfidence()>40){
                    best = activity;
                }
            }
        }
        if (best!=null)
            broadcastActivity(best);
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(INTENT_STR);
        intent.putExtra("type", activity.getType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}