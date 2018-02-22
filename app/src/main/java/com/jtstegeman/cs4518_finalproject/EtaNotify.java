package com.jtstegeman.cs4518_finalproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by jtste on 2/21/2018.
 */

public class EtaNotify {
    private static EtaNotify ourInstance = null;

    public synchronized static EtaNotify getInstance(Context ctx) {
        if (ourInstance==null)
            ourInstance = new EtaNotify(ctx);
        return ourInstance;
    }

    private String CHANNEL_ID = "com.jstegeman.cs4518";
    private static int id = 0;

    private EtaNotify(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "CS4518 Late Notifier";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for when you are late");
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void publishNotification(Context ctx, String title, String content){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifiation_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(id++, mBuilder.build());
    }
}
