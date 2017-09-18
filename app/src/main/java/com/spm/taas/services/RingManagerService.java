package com.spm.taas.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.spm.taas.DialerPanel;
import com.spm.taas.LandingActivity;
import com.spm.taas.R;

/**
 * Created by saikatpakira on 18/09/17.
 */

public class RingManagerService extends Service {

    private static final String ACTION_START = "com.sp.taas.ACTION_START";
    private static final String ACTION_STOP = "com.sp.taas.ACTION_STOP";
    private static final String ACTION_PAUSE = "com.sp.taas.ACTION_PAUSE";
    private static final String ACTION_RESUME = "com.sp.taas.ACTION_RESUME";

    private Ringtone ring = null;
    private static boolean isSrviceRunning=false;

    @Override
    public void onCreate() {
        super.onCreate();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ring = RingtoneManager.getRingtone(getApplicationContext(), notification);

    }


    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction() == ACTION_START) {
                if(!isSrviceRunning){
                    Log.i("hereweare","Action start....");
                    makeForeground("Testing Main Data....");
                }
            } else {
                stopMe();
            }
        }


        return START_NOT_STICKY;
    }


    private void makeForeground(String messageBody) {

        manageRing(true);
        isSrviceRunning=true;

        Intent intent = new Intent(this, DialerPanel.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TAAS")
                .setContentText(messageBody)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(Notification.VISIBILITY_PUBLIC)
                .setContentText("Heads-Up Notification on Android L or above.").setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true);


        startForeground(1, notificationBuilder.build());



//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }





    public void stopMe() {
        manageRing(false);
        isSrviceRunning=false;
        stopForeground(true);
        stopSelf();
    }


    public void manageRing(boolean flag) {


        Log.i("hereweare","Action Play ...."+flag);

        if (flag) {
            ring.play();
        } else {
            ring.stop();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
