package com.jtstegeman.cs4518_finalproject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;

/**
 * Created by kyle on 2/27/18.
 */

public class BackgroundService extends Service {

    public static boolean isRunning = false;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private Intent mIntent;
    private PendingIntent mPendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntent = new Intent(this, DetectedActivitiesIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(3000, mPendingIntent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
