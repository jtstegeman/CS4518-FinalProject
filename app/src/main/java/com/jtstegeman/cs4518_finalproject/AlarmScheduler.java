package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.jtstegeman.cs4518_finalproject.database.AlarmHelper;
import com.jtstegeman.cs4518_finalproject.database.AlarmObject;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETAFactory;
import com.jtstegeman.cs4518_finalproject.etaSystem.ETASystem;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;
import com.jtstegeman.cs4518_finalproject.sms.TextMessageHandler;
import com.jtstegeman.cs4518_finalproject.weather.WeatherManager;
import com.jtstegeman.cs4518_finalproject.weather.WeatherType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static boolean schedule(AlarmObject alarm, Context ctx){
        AlarmScheduler s =new AlarmScheduler(alarm);
        return s.schedule(ctx);
    }

    public static boolean cancel(AlarmObject alarm, Context ctx){
        AlarmScheduler s = new AlarmScheduler(alarm);
        return s.cancel(ctx);
    }

    public boolean schedule(Context ctx){
        UserLocation.getInstance(ctx).refresh(ctx);
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
        Date now = calendar.getTime();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        ETASystem eta = ETAFactory.getDefaultETASystem(prefs);
        Location targetLocation = new Location("");
        targetLocation.setLatitude(a.getLatitude());
        targetLocation.setLongitude(a.getLongitude());
        UserActivity currentActivity = DetectedActivitiesIntentService.getCurrentActivity(ctx);

        WeatherType weather = WeatherManager.getInstance(ctx).getWeather(ctx);

        long travelSeconds = eta.calculateTravelTime(targetLocation, UserLocation.getLocation(ctx), currentActivity);

        if(weather != null) {
            travelSeconds *= weather.getTimeMultiplier();
            Log.i("Weather", weather + " condition, applying multiplier of " + weather.getTimeMultiplier() + " to make time " + travelSeconds);
        }

        long travelMillis = travelSeconds*1000;

        if (travelMillis<15000){
            EtaNotify.getInstance(ctx).publishNotification(ctx,"You made it!!!", "You made it to the meeting: "+a.getName());
            AlarmHelper.getInstance(ctx).delete(a);
            return true;
        }

        long leaveTime = a.getDesiredArrivalTime().getTime()-travelMillis;

        DateFormat d = new SimpleDateFormat();
        String arivalTime = d.format(new Date(System.currentTimeMillis()+travelMillis));

        long nextTime = leaveTime;

        if (leaveTime<System.currentTimeMillis()){
            if (a.getNotificationState()<AlarmObject.WARNING_LATE) {
                a.setNotificationState(AlarmObject.WARNING_LATE);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"You are late!!!", "You have to hurry up to make the meeting: "+a.getName()+"\nExpected Arival: "+arivalTime);
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && prefs.getBoolean(ctx.getString(R.string.pref_sms_blast_key), false)) {
                    TextMessageHandler.sendSMS(a.getPhoneNumbers(), "I'm going to be late to the meeting, I will probably arrive at " + arivalTime);
                }
            }
            nextTime = System.currentTimeMillis() + 30000;
        }
        else if (leaveTime<System.currentTimeMillis()+60000){
            if (a.getNotificationState()<AlarmObject.WARNING_1_MIN) {
                a.setNotificationState(AlarmObject.WARNING_1_MIN);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"1 Minute Warning", "You have less than 1 minute until you need to leave for the meeting: "+a.getName()+"\nExpected Arival: "+arivalTime);
            }
            else if (a.getNotificationState()==AlarmObject.WARNING_LATE){
                a.setNotificationState(AlarmObject.WARNING_1_MIN);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"Back on Time", "You made up some time, but you are still almost late for: "+a.getName()+"\nExpected Arival: "+arivalTime);
            }
            nextTime = System.currentTimeMillis() + 30000;
        }
        else if (leaveTime<System.currentTimeMillis()+30000){
            if (a.getNotificationState()<AlarmObject.WARNING_5_MIN) {
                a.setNotificationState(AlarmObject.WARNING_5_MIN);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"5 Minute Warning", "You have 5 minutes until you need to leave for the meeting: "+a.getName()+"\nExpected Arival: "+arivalTime);
            }
            else if (a.getNotificationState()==AlarmObject.WARNING_1_MIN){
                a.setNotificationState(AlarmObject.WARNING_5_MIN);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"Back on Track", "You made up some time, you can slow down some and still make the meeting: "+a.getName()+"\nExpected Arival: "+arivalTime);
            }
            else if (a.getNotificationState()==AlarmObject.WARNING_LATE){
                a.setNotificationState(AlarmObject.WARNING_5_MIN);
                AlarmHelper.getInstance(ctx).update(a);
                EtaNotify.getInstance(ctx).publishNotification(ctx,"Back on Track", "WOW!!! You can move fast. You should make the meeting: "+a.getName()+"\nExpected Arival: "+arivalTime);
            }
            nextTime = System.currentTimeMillis() + 60000;
        }
        else {
            if (leaveTime-System.currentTimeMillis()<600000){ // if less than 10 minutes away
                nextTime = System.currentTimeMillis() + 60000;
            }else { // next alarm is halfway between now and when they need to leave
                nextTime = leaveTime-60000*20;
            }
        }
        calendar.setTime(new Date(nextTime));
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pendingIntent);
        Log.i("ALR","Scheduled: "+d.format(new Date(nextTime)));
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
        AlarmObject alarm = AlarmHelper.getInstance(context).get(alarmName);
        if (alarm!=null) {
            String location = alarm.getLocation();
            Toast.makeText(context, "Alarm: " + alarmName + " went off at " + location, Toast.LENGTH_LONG).show();
            AlarmScheduler.schedule(alarm, context);
        }
    }

    public String getEstimatedArival(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        ETASystem eta = ETAFactory.getDefaultETASystem(prefs);
        Location targetLocation = new Location("");
        targetLocation.setLatitude(a.getLatitude());
        targetLocation.setLongitude(a.getLongitude());
        UserActivity currentActivity = DetectedActivitiesIntentService.getCurrentActivity(ctx);
        long travelSeconds = eta.calculateTravelTime(targetLocation, UserLocation.getLocation(ctx), currentActivity);

        long travelMillis = travelSeconds*1000;

        DateFormat d = new SimpleDateFormat();
        return d.format(new Date(System.currentTimeMillis()+travelMillis));
    }
}
