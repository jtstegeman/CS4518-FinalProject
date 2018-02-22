package com.jtstegeman.cs4518_finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jtstegeman.cs4518_finalproject.etaSystem.UserActivity;

/**
 * Created by jtste on 2/21/2018.
 */

public class UserLocation {

    private static UserLocation local = null;
    private final LocationCallback mLocationCallback;
    private final FusedLocationProviderClient mFusedLocationClient;
    private boolean setup = false;
    private boolean sentNot = false;
    private static Location last = null;

    public static UserLocation getInstance(Context ctx) {
        if (local == null)
            local = new UserLocation(ctx);
        return local;
    }

    private UserLocation(Context ctx) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationResult.getLastLocation();
            }

            ;
        };
        setup(ctx);
    }

    private void setup(Context ctx){
        if (setup)
            return;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ctx,"Allow Location Updates", Toast.LENGTH_SHORT).show();
            if (ctx instanceof Activity) {
                ActivityCompat.requestPermissions((Activity)ctx, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                if (!sentNot) {
                    sentNot = true;
                    EtaNotify.getInstance(ctx).publishNotification(ctx, "Allow Notifications", "We can't function without access to your location to know you are late!!!");
                }
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            setup = true;
        }
    }

    public void refresh(Context ctx){
        setup(ctx);
    }

    public static void setLocation(Location location, Context ctx){
        last = location;
        SharedPreferences settings = ctx.getSharedPreferences("App", Context.MODE_PRIVATE);
        settings.edit().putFloat("lat", (float)location.getLatitude()).putFloat("lng",(float)location.getLongitude());
    }
    public static Location getLocation(Context ctx){
        if (last!=null)
            return last;
        SharedPreferences settings = ctx.getSharedPreferences("App", Context.MODE_PRIVATE);
        double lat = settings.getFloat("lat",0);
        double lng = settings.getFloat("lng",0);
        Location l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lng);
        last = l;
        return l;
    }
}
