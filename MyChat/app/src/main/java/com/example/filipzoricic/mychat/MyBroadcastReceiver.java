package com.example.filipzoricic.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by filipzoricic on 11/5/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    Bitmap bitmap;
    Activity activity;
    MyBrodcastReceiverInterface myBrodcastReceiverInterface;

    public interface MyBrodcastReceiverInterface{
        public void OnLoad(MessageData messageData);
    }

    public MyBroadcastReceiver(Activity activity){
        this.activity=activity;
    }

    public MyBroadcastReceiver(Activity activity, MyBrodcastReceiverInterface myBrodcastReceiverInterface){
        this.activity=activity;
        this.myBrodcastReceiverInterface=myBrodcastReceiverInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MessageData messageData = new MessageData();

        try {
            messageData.setData(intent.getStringExtra("data"));
            messageData.setFrom(intent.getStringExtra("from"));
            messageData.setTo(intent.getStringExtra("to"));
            messageData.setStatus(intent.getIntExtra("status",0));
            messageData.setType(intent.getStringExtra("type"));
            myBrodcastReceiverInterface.OnLoad(messageData);
        }catch (Exception e){
            Log.i("eee6",e.toString());
        }
    }


}
