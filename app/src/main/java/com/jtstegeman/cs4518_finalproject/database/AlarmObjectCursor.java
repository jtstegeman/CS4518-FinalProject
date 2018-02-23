package com.jtstegeman.cs4518_finalproject.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jtste on 2/15/2018.
 */

public class AlarmObjectCursor extends CursorWrapper {
    public AlarmObjectCursor(Cursor cursor) {
        super(cursor);
    }

    public AlarmObject getAlarmObject() {
        String name = "Default Alarm";
        String location = "";
        String smsNumbers="";
        double lat = 42.274456;
        double lng =-71.806722;
        long date = System.currentTimeMillis();
        long bufTime=5*60000;
        int notState = 0;
        try {
            name = getString(getColumnIndex(AlarmObject.DB_NAME));
            location = getString(getColumnIndex(AlarmObject.DB_LOCATION));
            date = getLong(getColumnIndex(AlarmObject.DB_TIME));
            lng = getDouble(getColumnIndex(AlarmObject.DB_LNG));
            lat = getDouble(getColumnIndex(AlarmObject.DB_LAT));
            bufTime = getLong(getColumnIndex(AlarmObject.DB_EXTRA_TIME));
            notState = getInt(getColumnIndex(AlarmObject.DB_NOTI_STATE));
            smsNumbers = getString(getColumnIndex(AlarmObject.DB_SMS));
        } catch (Exception e){}

        AlarmObject alrm = new AlarmObject(name);
        alrm.setBufferTime_ms(bufTime);
        alrm.setLatitude(lat);
        alrm.setLongitude(lng);
        alrm.setLocation(location);
        alrm.setTime(new Date(date));
        alrm.setNotificationState(notState);
        alrm.setPhoneNumbers(AlarmObject.decompressStrings(smsNumbers));
        return alrm;
    }
}
