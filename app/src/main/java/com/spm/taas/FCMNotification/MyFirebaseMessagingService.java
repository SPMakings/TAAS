package com.spm.taas.FCMNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spm.taas.LandingActivity;
import com.spm.taas.R;
import com.spm.taas.services.RingManagerService;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Saikat Pakira on 15/05/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        try {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.i("MyFirebaseMsgService", object.toString());

            /*
            * {
"token":"T1==cGFydG5lcl9pZD00NTc4NDQzMiZzaWc9ZTYwN2ZiNjNlYjBkNTU3NWQ3MDVlMGU2OWRlZjFlNzZhYTZhY2YyYTpzZXNzaW9uX2lkPTJfTVg0ME5UYzRORFF6TW41LU1UUTVORGsxT0RZNU56a3pNSDVvU1hJNVdWZHZiRFJCTXpkQ1Uzb3ZZM0JOZURCaloyeC1RWDQmY3JlYXRlX3RpbWU9MTQ5NDk1ODY5NyZyb2xlPXB1Ymxpc2hlciZub25jZT0xNDk0OTU4Njk3LjkxMzAwNzgxNTg=",
"message":"Video Call Request",
"session_id":"2_MX40NTc4NDQzMn5-MTQ5NDk1ODY5NzkzMH5oSXI5WVdvbDRBMzdCU3ovY3BNeDBjZ2x-QX4"
}
            *
            * */

            //sendNotification(object.getString("message"));
            Intent i=new Intent(this, RingManagerService.class);
            i.setAction("com.sp.taas.ACTION_START");
            startService(i);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getBody());
//        }

    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TAAS")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(Notification.VISIBILITY_PUBLIC)
                .setSound(defaultSoundUri).setFullScreenIntent(pendingIntent,true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
