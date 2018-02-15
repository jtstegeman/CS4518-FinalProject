package com.jtstegeman.cs4518_finalproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jtste on 2/15/2018.
 */

public class AlarmHelper {
    private static AlarmHelper ourInstance =null;

    public synchronized static AlarmHelper getInstance(Context context) {
        if (ourInstance == null){
             ourInstance = new AlarmHelper(context);
        }
        return ourInstance;
    }

    private Context ctx;
    private SQLiteDatabase db;
    private AlarmHelper(Context context) {
        ctx = context.getApplicationContext();
        db = new SQLHelper(ctx).getWritableDatabase();
    }

    public void create(AlarmObject alarm) {
        ContentValues values = getContentValues(alarm);
        db.insert(AlarmObject.DB_ALARM_TABLE, null, values);
    }

    public void update(AlarmObject alarm) {
        ContentValues values = getContentValues(alarm);
        db.update(AlarmObject.DB_ALARM_TABLE, values,
                AlarmObject.DB_NAME + " = ?",
                new String[] { alarm.getName() });
    }

    public void write(AlarmObject alarm){
        if (get(alarm.getName())==null){
            create(alarm);
        }
        else{
            update(alarm);
        }
    }

    public AlarmObject get(String name){
        AlarmObjectCursor cursor = queryCrimes(
                AlarmObject.DB_NAME + " = ?",
                new String[] { name }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getAlarmObject();
        } finally {
            cursor.close();
        }
    }

    public List<AlarmObject> getAlarms(){
        AlarmObjectCursor cursor = queryCrimes(null,null);
        List<AlarmObject> lst = new ArrayList<>();
        try {
            if (cursor.getCount() == 0) {
                return lst;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lst.add(cursor.getAlarmObject());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return lst;
    }

    public AlarmObject getNextAlarm(){
        List<AlarmObject> alarms = this.getAlarms();
        if (alarms.isEmpty())
            return null;
        Collections.sort(alarms, new Comparator<AlarmObject>() {
            @Override
            public int compare(AlarmObject alarmObject, AlarmObject t1) {
                return alarmObject.getTime().compareTo(t1.getTime());
            }
        });
        return alarms.get(0);
    }

    private static ContentValues getContentValues(AlarmObject alarm) {
        ContentValues values = new ContentValues();
        values.put(AlarmObject.DB_EXTRA_TIME, alarm.getBufferTime_ms());
        values.put(AlarmObject.DB_LAT, alarm.getLatitude());
        values.put(AlarmObject.DB_LNG, alarm.getLongitude());
        values.put(AlarmObject.DB_LOCATION, alarm.getLocation());
        values.put(AlarmObject.DB_NAME, alarm.getName());
        values.put(AlarmObject.DB_TIME, alarm.getTime().getTime());
        return values;
    }

    private AlarmObjectCursor queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                AlarmObject.DB_ALARM_TABLE,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new AlarmObjectCursor(cursor);
    }

}
