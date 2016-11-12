package com.example.filipzoricic.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity{
    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    ViewPagerAdapter viewPagerAdapter;
    ContactListFragment contactListFragment;
    MessagesListFragment messagesListFragment;
    DatabaseHandler databaseHandler;
    DataProvider dataProvider;
    BroadcastReceiver messegingServiceRecever;
    Context context;
    Activity activity;
    String username;
    boolean logout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        activity =this;
        context = this;
        dataProvider = new DataProvider(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        databaseHandler = new DatabaseHandler(this);
        contactListFragment = new ContactListFragment();
        messagesListFragment = new MessagesListFragment();
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(contactListFragment,"Contacts");
        viewPagerAdapter.addFragment(messagesListFragment,"Conversation");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


        messegingServiceRecever = new MyBroadcastReceiver(this, new MyBroadcastReceiver.MyBrodcastReceiverInterface() {
            @Override
            public void OnLoad(MessageData messageData) {
                //databaseHandler.putMessage(messageData);
                //StaticMethods.createNotification(messageData,context);
                messagesListFragment.setMesageDatas(databaseHandler.getMessagesByContacts());
            }
        });

        viewPager.setCurrentItem(1);

        Intent intent = this.getIntent();
        String id = intent.getStringExtra("message_id");
        username = intent.getStringExtra("username");
        if(id!=null && username!=null){
            Log.e("eee1","trazim "+id);
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action","downloadAll");
                jsonObject.put("username",username);
                final DataProvider dataProvider = new DataProvider(this, new DataProvider.DataProviderInterface() {
                    @Override
                    public void onLoad(JSONObject jsonObject) {
                        try {
                            Log.i("qqq",jsonObject.getJSONArray("ProductsData").toString()+" tii");
                            AsyncTask asyncTask= databaseHandler.saveMessages();
                            asyncTask.execute(jsonObject.getJSONArray("ProductsData"), new DatabaseHandler.OnLoadMessageInterface() {
                                @Override
                                public void onLoad(ArrayList<MessageData> newlist) {
                                    messagesListFragment.setMesageDatas(databaseHandler.getMessagesByContacts());
                                }
                            },  activity);

                        }catch (Exception e){Log.i("qqq",e.toString());}
                    }
                });
                dataProvider.sendData(jsonObject);
            }catch (Exception e){}
        }

        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_EXTERNAL_STORAGE = 1;
            String[] PERMISSIONS_STORAGE = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //LocalBroadcastManager.getInstance(this).registerReceiver(messegingServiceRecever, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        registerReceiver(messegingServiceRecever, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(messegingServiceRecever);
        unregisterReceiver(messegingServiceRecever);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0 && contactListFragment.linearLayout.getVisibility()==View.VISIBLE){
            contactListFragment.linearLayout.setVisibility(View.GONE);
            contactListFragment.button.setVisibility(View.VISIBLE);
        }else {
            if(logout)
                onBackPressed();
            else
                moveTaskToBack(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                databaseHandler.setUsername("");
                logout=true;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("logout","logout");
                    dataProvider.sendData(jsonObject);
                }catch (Exception e){}
                onBackPressed();
                break;
        }
        return true;
    }


}
