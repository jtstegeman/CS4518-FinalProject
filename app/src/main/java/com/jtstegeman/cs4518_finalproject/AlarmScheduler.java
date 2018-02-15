package com.jtstegeman.cs4518_finalproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;

import java.util.Calendar;
import java.util.Objects;

/**
 * Created by jtste on 2/15/2018.
 */

public class AlarmScheduler extends BroadcastReceiver {

    public static final Class ALARM_SCHED_INTENT = AlarmScheduler.class;
    public static final String ALARM_SCHED_INTENT_EXTRA_ALARMNAME = "wakeup.name";

    private static AlarmManager alarmManager = null;

    private AlarmObject a = null;
    public AlarmScheduler(AlarmObject alarm){
        a = alarm;
    }
    public AlarmScheduler(){
    }

    public boolean schedule(Context ctx){
        if (alarmManager==null)
            alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager==null || a==null){
            return false;
        }
        AlarmHelper.getInstance(ctx).write(a);
        Intent intent = new Intent(ctx,ALARM_SCHED_INTENT);
        intent.putExtra(ALARM_SCHED_INTENT_EXTRA_ALARMNAME, a.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, Objects.hashCode(a.getName()), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(a.getTime());
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.i("ALR","Scheduled");
        return true;
    }

    public boolean cancel(Context ctx){
        if (alarmManager==null)
            alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager==null || a==null){
            return false;
        }
        Intent intent = new Intent(ctx,ALARM_SCHED_INTENT);
        intent.putExtra(ALARM_SCHED_INTENT_EXTRA_ALARMNAME, a.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, Objects.hashCode(a.getName()), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(a.getTime());
        alarmManager.cancel(pendingIntent);
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALR","REC");
        String alarmName = intent.getStringExtra(ALARM_SCHED_INTENT_EXTRA_ALARMNAME);
        String location = AlarmHelper.getInstance(context).get(alarmName).getLocation();
        Toast.makeText(context, "Alarm: "+alarmName+" went off at "+location,Toast.LENGTH_LONG).show();
    }
}
