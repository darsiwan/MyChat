package com.example.filipzoricic.mychat;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by filipzoricic on 10/14/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static final String INTENT_FILTER = "INTENT_FILTER2";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i("eee", refreshedToken);
        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra("token",refreshedToken);
        sendBroadcast(intent);
    }


}
