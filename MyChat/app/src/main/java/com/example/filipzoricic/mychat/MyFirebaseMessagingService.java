package com.example.filipzoricic.mychat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by filipzoricic on 10/14/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final Map<String, String> map = remoteMessage.getData();


        final DatabaseHandler databaseHandler = new DatabaseHandler(this);

        DataProvider dataProvider = new DataProvider(this, new DataProvider.DataProviderInterface() {
            @Override
            public void onLoad(JSONObject jsonObject) {
                try {
                    Log.i("eee6",jsonObject.toString());
                    MessageData messageData = new MessageData();
                    messageData.setFrom(jsonObject.getString("user_from"));
                    messageData.setTo(jsonObject.getString("user_to"));
                    messageData.setType(jsonObject.getString("type"));
                    messageData.setStatus(2);
                    if(messageData.getType().equals("image")){
                        byte[] bytes = Base64.decode(jsonObject.getString("data"),Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        messageData.bitmap=bitmap;
                        messageData.setData(DatabaseHandler.saveBitmap(bitmap,"recived",context));
                    }else{
                        messageData.setData(jsonObject.getString("data"));
                    }
                    databaseHandler.putMessage(messageData);
                    Intent intent = new Intent(INTENT_FILTER);
                    intent.putExtra("from",messageData.getFrom());
                    intent.putExtra("to", messageData.getTo());
                    intent.putExtra("data",messageData.getData());
                    intent.putExtra("type",messageData.getType());
                    intent.putExtra("status",messageData.getStatus());
                    sendBroadcast(intent);
                }
                catch (Exception e){ Log.i("eee6",e.toString());}
            }
        });
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("action","download");
            jsonObject.put("message_id",map.get("message_id"));
        }catch (Exception e){}
        dataProvider.sendData(jsonObject);


    }


}
