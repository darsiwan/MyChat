package com.example.filipzoricic.mychat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements DataProvider.DataProviderInterface{

    BroadcastReceiver messegingServiceIDRecever;
    DatabaseHandler databaseHandler;
    DataProvider dataProvider;
    EditText editTextMail;
    EditText editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHandler = new DatabaseHandler(this);
        String username = databaseHandler.getUsername();
        if(databaseHandler.getUsername()!=null && username.length()>0){
            Log.i("eee","Logiran");
            Intent startingIntent = getIntent();
            Intent intent = new Intent(this,MainMenuActivity.class);
            if (startingIntent != null) {
                String id = startingIntent.getStringExtra("message_id");
                intent.putExtra("message_id",id);
                intent.putExtra("username",username);
            }
            startActivity(intent);


        }

        dataProvider = new DataProvider(this, this);
        editTextMail = (EditText) findViewById(R.id.mail_login);
        editTextPass = (EditText) findViewById(R.id.password_login);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = FirebaseInstanceId.getInstance().getToken();
                String mail = editTextMail.getText().toString();
                String password = editTextPass.getText().toString();

                Log.i("eee3",mail+" i "+token);
                if(!token.isEmpty()&&!mail.isEmpty()&&!password.isEmpty())
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("action","login");
                        jsonObject.put("password",password);
                        jsonObject.put("token",token);
                        jsonObject.put("mail",mail);
                        dataProvider.sendData(jsonObject);
                    }catch (Exception e){}
            }
        });

       /* messegingServiceIDRecever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Bundle bundle = intent.getExtras();

                //String token =  intent.getStringExtra("token");
                //databaseHandler.setToken(token);
            }
        };
        registerReceiver(messegingServiceIDRecever, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
*/
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(messegingServiceIDRecever);
        super.onDestroy();
    }


    @Override
    public void onLoad(JSONObject jsonObject) {
        if(jsonObject!=null)
            try{
                Log.i("eee","onLoad");
                if(jsonObject.getInt("result")==1){
                    databaseHandler.setUsername(jsonObject.getString("username"));
                    Intent i = new Intent(this, MainMenuActivity.class);
                    startActivity(i);
                }else{
                    Log.i("eeee",jsonObject.toString());
                    //Intent i = new Intent(this, MainMenuActivity.class);
                    //startActivity(i);
                }

            }catch (Exception e){Log.i("eee",e.toString());}
    }
}
