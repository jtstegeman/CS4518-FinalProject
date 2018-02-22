package com.jtstegeman.cs4518_finalproject.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.jtstegeman.cs4518_finalproject.database.AlarmObject.DB_ALARM_TABLE;

/**
 * Created by jtste on 2/15/2018.
 */

public class SQLHelper extends SQLiteOpenHelper {

    private static final String TAG = "AlarmSQLHelper";
    private static final int VERSION = 8;
    private static final String DATABASE_NAME = "alarmsbase.db";

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AlarmObject.DB_ALARM_TABLE + "(" +
                " _id integer primary key autoincrement, " +
                AlarmObject.DB_TIME + ", " +
                AlarmObject.DB_EXTRA_TIME + ", " +
                AlarmObject.DB_LAT + ", " +
                AlarmObject.DB_LNG + ", " +
                AlarmObject.DB_NOTI_STATE + ", " +
                AlarmObject.DB_LOCATION + ", " +
                AlarmObject.DB_NAME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newVersion) {
        db.execSQL("drop table "+AlarmObject.DB_ALARM_TABLE);
        this.onCreate(db);
    }
}
