package com.example.filipzoricic.mychat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by filipzoricic on 11/7/16.
 */
public class StaticMethods {

    public static void createNotification(MessageData messageData, Context context){

        Intent intent = new Intent(context, ResultActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setContentTitle(messageData.getFrom());
        nBuilder.setContentText(messageData.getData());
        nBuilder.setAutoCancel(true);
        nBuilder.setSound(uri);
        nBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, nBuilder.build());


    }
}
